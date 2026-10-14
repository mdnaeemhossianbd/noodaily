package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.Category
import com.example.data.model.Post
import com.example.data.model.Subcategory
import com.example.data.seed.InitialDataSeed
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject

class ContentRepository(private val context: Context) {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(
        context.getString(R.string.firestore_database_id)
    )

    companion object {
        private const val TAG = "ContentRepository"
        private const val PREFS_NAME = "noor_local_content_store"
        private const val KEY_CUSTOM_POSTS = "cached_custom_posts"

        // In-memory reactive state flow of locally saved/created posts
        private val localPostsState = MutableStateFlow<List<Post>>(emptyList())
        private var isLocalLoaded = false
    }

    init {
        // Load offline cached posts on first instantiation
        synchronized(this) {
            if (!isLocalLoaded) {
                loadLocalPostsFromPrefs()
                isLocalLoaded = true
            }
        }

        // Asynchronously check and seed initial categories and posts to Firestore if empty
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialDataIfNeeded()
        }
    }

    private fun loadLocalPostsFromPrefs() {
        try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val jsonStr = prefs.getString(KEY_CUSTOM_POSTS, null) ?: return
            val array = JSONArray(jsonStr)
            val list = mutableListOf<Post>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    Post(
                        id = obj.optString("id"),
                        title = obj.optString("title"),
                        titleBn = obj.optString("titleBn"),
                        subtitle = obj.optString("subtitle"),
                        arabicText = obj.optString("arabicText"),
                        bengaliText = obj.optString("bengaliText"),
                        englishText = obj.optString("englishText"),
                        transliteration = obj.optString("transliteration"),
                        mainContent = obj.optString("mainContent"),
                        categoryId = obj.optString("categoryId", "cat_quran"),
                        subcategoryId = obj.optString("subcategoryId"),
                        featuredImage = obj.optString("featuredImage"),
                        audioUrl = obj.optString("audioUrl"),
                        sourceReference = obj.optString("sourceReference"),
                        author = obj.optString("author", "Noor Daily Editorial"),
                        isFeatured = obj.optBoolean("isFeatured", false),
                        isPublished = obj.optBoolean("isPublished", true),
                        viewCount = obj.optLong("viewCount", 0L)
                    )
                )
            }
            localPostsState.value = list
            Log.d(TAG, "Loaded ${list.size} offline custom posts")
        } catch (e: Exception) {
            Log.e(TAG, "Error loading local posts from prefs: ${e.message}")
        }
    }

    private fun saveLocalPostsToPrefs(posts: List<Post>) {
        try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val array = JSONArray()
            posts.forEach { p ->
                val obj = JSONObject().apply {
                    put("id", p.id)
                    put("title", p.title)
                    put("titleBn", p.titleBn)
                    put("subtitle", p.subtitle)
                    put("arabicText", p.arabicText)
                    put("bengaliText", p.bengaliText)
                    put("englishText", p.englishText)
                    put("transliteration", p.transliteration)
                    put("mainContent", p.mainContent)
                    put("categoryId", p.categoryId)
                    put("subcategoryId", p.subcategoryId)
                    put("featuredImage", p.featuredImage)
                    put("audioUrl", p.audioUrl)
                    put("sourceReference", p.sourceReference)
                    put("author", p.author)
                    put("isFeatured", p.isFeatured)
                    put("isPublished", p.isPublished)
                    put("viewCount", p.viewCount)
                }
                array.put(obj)
            }
            prefs.edit().putString(KEY_CUSTOM_POSTS, array.toString()).apply()
            localPostsState.value = posts
        } catch (e: Exception) {
            Log.e(TAG, "Error saving local posts to prefs: ${e.message}")
        }
    }

    private suspend fun seedInitialDataIfNeeded() {
        try {
            val categoriesSnapshot = db.collection("categories").limit(1).get().await()
            if (categoriesSnapshot.isEmpty) {
                Log.d(TAG, "Firestore is empty, seeding initial categories and posts...")
                val batch = db.batch()
                InitialDataSeed.categories.forEach { cat ->
                    val docRef = db.collection("categories").document(cat.id)
                    batch.set(docRef, cat)
                }
                InitialDataSeed.subcategories.forEach { sub ->
                    val docRef = db.collection("subcategories").document(sub.id)
                    batch.set(docRef, sub)
                }
                InitialDataSeed.posts.forEach { post ->
                    val docRef = db.collection("posts").document(post.id)
                    batch.set(docRef, post)
                }
                batch.commit().await()
                Log.d(TAG, "Initial data successfully seeded to Firestore!")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Note: Firestore seeding skipped or pending: ${e.message}")
        }
    }

    fun observeCategories(): Flow<List<Category>> {
        return db.collection("categories")
            .whereEqualTo("isActive", true)
            .snapshots()
            .map { snapshot ->
                val list = snapshot.documents.mapNotNull { doc ->
                    try { doc.toObject(Category::class.java) } catch (_: Exception) { null }
                }
                if (list.isEmpty()) InitialDataSeed.categories else list.sortedBy { it.order }
            }
            .catch { e ->
                handleFirestoreError(e as Exception, OperationType.LIST, "categories")
                emit(InitialDataSeed.categories)
            }
    }

    fun observeSubcategories(categoryId: String): Flow<List<Subcategory>> {
        return db.collection("subcategories")
            .whereEqualTo("categoryId", categoryId)
            .whereEqualTo("isActive", true)
            .snapshots()
            .map { snapshot ->
                val list = snapshot.documents.mapNotNull { doc ->
                    try { doc.toObject(Subcategory::class.java) } catch (_: Exception) { null }
                }
                if (list.isEmpty()) {
                    InitialDataSeed.subcategories.filter { it.categoryId == categoryId }
                } else {
                    list.sortedBy { it.order }
                }
            }
            .catch { e ->
                handleFirestoreError(e as Exception, OperationType.LIST, "subcategories")
                emit(InitialDataSeed.subcategories.filter { it.categoryId == categoryId })
            }
    }

    private fun mapDocToPost(doc: com.google.firebase.firestore.DocumentSnapshot): Post? {
        try {
            val p = doc.toObject(Post::class.java)
            if (p != null) return p.copy(id = doc.id)
        } catch (_: Exception) {}

        val data = doc.data ?: return null
        val titleBn = data["titleBn"] as? String ?: (data["title"] as? String ?: "")
        val title = data["title"] as? String ?: titleBn
        if (titleBn.isBlank() && title.isBlank()) return null

        return Post(
            id = doc.id,
            title = title,
            titleBn = titleBn,
            subtitle = data["subtitle"] as? String ?: "",
            arabicText = data["arabicText"] as? String ?: "",
            bengaliText = data["bengaliText"] as? String ?: "",
            englishText = data["englishText"] as? String ?: "",
            transliteration = data["transliteration"] as? String ?: "",
            mainContent = data["mainContent"] as? String ?: "",
            categoryId = data["categoryId"] as? String ?: "cat_quran",
            subcategoryId = data["subcategoryId"] as? String ?: "",
            featuredImage = data["featuredImage"] as? String ?: "",
            audioUrl = data["audioUrl"] as? String ?: "",
            sourceReference = data["sourceReference"] as? String ?: "",
            author = data["author"] as? String ?: "Noor Daily Editorial Board",
            isFeatured = (data["isFeatured"] as? Boolean) ?: false,
            isPublished = (data["isPublished"] as? Boolean) ?: true,
            viewCount = (data["viewCount"] as? Number)?.toLong() ?: 0L
        )
    }

    fun observeFeaturedPosts(): Flow<List<Post>> {
        val firestoreFlow = db.collection("posts")
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc -> mapDocToPost(doc) }
                    .filter { it.isPublished && it.isFeatured }
            }
            .catch { e ->
                handleFirestoreError(e as Exception, OperationType.LIST, "posts")
                emit(emptyList())
            }

        return combine(firestoreFlow, localPostsState) { firestoreList, localList ->
            val featuredLocal = localList.filter { it.isFeatured && it.isPublished }
            val map = mutableMapOf<String, Post>()
            // Seed defaults first
            InitialDataSeed.posts.filter { it.isFeatured }.forEach { map[it.id] = it }
            // Overlay Firestore
            firestoreList.forEach { map[it.id] = it }
            // Overlay Local
            featuredLocal.forEach { map[it.id] = it }
            map.values.toList()
        }
    }

    fun observeLatestPosts(limit: Long = 50): Flow<List<Post>> {
        val firestoreFlow = db.collection("posts")
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc -> mapDocToPost(doc) }
                    .filter { it.isPublished }
            }
            .catch { e ->
                handleFirestoreError(e as Exception, OperationType.LIST, "posts")
                emit(emptyList())
            }

        return combine(firestoreFlow, localPostsState) { firestoreList, localList ->
            val map = linkedMapOf<String, Post>()

            // 1. Newly created local custom posts come FIRST so users see them immediately
            localList.filter { it.isPublished }.forEach { map[it.id] = it }

            // 2. Newly saved Firestore posts come next (from Web Admin or Cloud)
            firestoreList.forEach { map[it.id] = it }

            // 3. Default seed posts populate remaining
            InitialDataSeed.posts.forEach { if (!map.containsKey(it.id)) map[it.id] = it }

            map.values.take(limit.toInt())
        }
    }

    fun observePostsByCategory(categoryId: String): Flow<List<Post>> {
        val firestoreFlow = db.collection("posts")
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc -> mapDocToPost(doc) }
                    .filter { it.isPublished && it.categoryId == categoryId }
            }
            .catch { e ->
                handleFirestoreError(e as Exception, OperationType.LIST, "posts")
                emit(emptyList())
            }

        return combine(firestoreFlow, localPostsState) { firestoreList, localList ->
            val map = linkedMapOf<String, Post>()

            // Local posts for this category first
            localList.filter { it.categoryId == categoryId && it.isPublished }.forEach { map[it.id] = it }

            // Firestore posts
            firestoreList.forEach { map[it.id] = it }

            // Default seed posts for this category
            InitialDataSeed.posts.filter { it.categoryId == categoryId }.forEach {
                if (!map.containsKey(it.id)) map[it.id] = it
            }

            map.values.toList()
        }
    }

    fun observePostById(postId: String): Flow<Post?> = flow {
        // Check local memory first
        val local = localPostsState.value.firstOrNull { it.id == postId }
        if (local != null) {
            emit(local)
            return@flow
        }

        try {
            val doc = db.collection("posts").document(postId).get().await()
            val post = if (doc.exists()) (mapDocToPost(doc) ?: doc.toObject(Post::class.java)) else null
            val finalPost = post ?: InitialDataSeed.posts.firstOrNull { it.id == postId }
            emit(finalPost)

            if (doc.exists()) {
                db.collection("posts").document(postId)
                    .update("viewCount", FieldValue.increment(1))
            }
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, "posts/$postId")
            emit(InitialDataSeed.posts.firstOrNull { it.id == postId })
        }
    }

    fun searchPosts(query: String): Flow<List<Post>> {
        val trimmed = query.trim().lowercase()
        return observeLatestPosts(50).map { allPosts ->
            if (trimmed.isEmpty()) {
                allPosts
            } else {
                allPosts.filter { post ->
                    post.title.lowercase().contains(trimmed) ||
                        post.titleBn.lowercase().contains(trimmed) ||
                        post.arabicText.contains(trimmed) ||
                        post.bengaliText.lowercase().contains(trimmed) ||
                        post.mainContent.lowercase().contains(trimmed) ||
                        post.tags.any { it.lowercase().contains(trimmed) }
                }
            }
        }
    }

    // --- ADMIN / CONTENT MANAGEMENT OPERATIONS ---
    suspend fun syncAllDefaultPostsToFirestore(): Result<Int> = runCatching {
        var count = 0
        val batch = db.batch()
        InitialDataSeed.categories.forEach { cat ->
            val docRef = db.collection("categories").document(cat.id)
            batch.set(docRef, cat)
        }
        InitialDataSeed.subcategories.forEach { sub ->
            val docRef = db.collection("subcategories").document(sub.id)
            batch.set(docRef, sub)
        }
        InitialDataSeed.posts.forEach { post ->
            val docRef = db.collection("posts").document(post.id)
            batch.set(docRef, post)
            count++
        }
        batch.commit().await()
        count
    }

    suspend fun savePost(post: Post): Result<Unit> = runCatching {
        val docId = if (post.id.isNotEmpty()) post.id else "post_${System.currentTimeMillis()}"
        val postToSave = post.copy(
            id = docId,
            createdAt = post.createdAt ?: Timestamp.now(),
            updatedAt = Timestamp.now(),
            isPublished = true
        )

        // 1. Immediately save into local cache and persist to device
        val currentLocal = localPostsState.value.toMutableList()
        val existingIndex = currentLocal.indexOfFirst { it.id == docId }
        if (existingIndex >= 0) {
            currentLocal[existingIndex] = postToSave
        } else {
            currentLocal.add(0, postToSave)
        }
        saveLocalPostsToPrefs(currentLocal)
        Log.d(TAG, "Post immediately cached locally: $docId (${postToSave.titleBn})")

        // 2. Persist to Firestore Cloud
        try {
            db.collection("posts").document(docId).set(postToSave).await()
            Log.d(TAG, "Post saved to Firestore cloud successfully: $docId")
        } catch (e: Exception) {
            Log.e(TAG, "Firestore write warning: ${e.message}", e)
            // Even if network or firestore has an issue, post was cached locally so user never loses it
        }
    }

    suspend fun deletePost(postId: String): Result<Unit> = runCatching {
        // Remove from local cache
        val currentLocal = localPostsState.value.filter { it.id != postId }
        saveLocalPostsToPrefs(currentLocal)

        // Remove from Firestore
        try {
            db.collection("posts").document(postId).delete().await()
        } catch (e: Exception) {
            Log.w(TAG, "Firestore delete error: ${e.message}")
        }
    }

    suspend fun saveCategory(category: Category): Result<Unit> = runCatching {
        val docId = if (category.id.isNotEmpty()) category.id else "cat_${System.currentTimeMillis()}"
        val categoryToSave = category.copy(id = docId)
        db.collection("categories").document(docId).set(categoryToSave).await()
    }

    suspend fun deleteCategory(categoryId: String): Result<Unit> = runCatching {
        db.collection("categories").document(categoryId).delete().await()
    }

    suspend fun saveSubcategory(subcategory: Subcategory): Result<Unit> = runCatching {
        val docId = if (subcategory.id.isNotEmpty()) subcategory.id else "sub_${System.currentTimeMillis()}"
        val subToSave = subcategory.copy(id = docId)
        db.collection("subcategories").document(docId).set(subToSave).await()
    }

    suspend fun deleteSubcategory(subcategoryId: String): Result<Unit> = runCatching {
        db.collection("subcategories").document(subcategoryId).delete().await()
    }
}
