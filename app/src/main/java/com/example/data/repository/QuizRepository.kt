package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.Quiz
import com.example.data.model.QuizQuestion
import com.example.data.seed.InitialDataSeed
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class QuizRepository(private val context: Context) {
    private val db = FirebaseFirestore.getInstance(
        context.getString(R.string.firestore_database_id)
    )

    fun observeQuizzes(): Flow<List<Quiz>> {
        return db.collection("quizzes")
            .whereEqualTo("isActive", true)
            .snapshots()
            .map { snapshot ->
                val list = snapshot.toObjects(Quiz::class.java)
                if (list.isEmpty()) InitialDataSeed.quizzes else list
            }
            .catch { e ->
                handleFirestoreError(e as Exception, OperationType.LIST, "quizzes")
                emit(InitialDataSeed.quizzes)
            }
    }

    fun observeQuizQuestions(quizId: String): Flow<List<QuizQuestion>> {
        return db.collection("quizQuestions")
            .whereEqualTo("quizId", quizId)
            .snapshots()
            .map { snapshot ->
                val firestoreList = snapshot.toObjects(QuizQuestion::class.java)
                val firestoreIds = firestoreList.map { it.id }.toSet()
                val remainingDefaults = InitialDataSeed.quizQuestions.filter { it.quizId == quizId && it.id !in firestoreIds }
                firestoreList + remainingDefaults
            }
            .catch { e ->
                handleFirestoreError(e as Exception, OperationType.LIST, "quizQuestions")
                emit(InitialDataSeed.quizQuestions.filter { it.quizId == quizId })
            }
    }

    fun observeAllQuizzes(): Flow<List<Quiz>> {
        return db.collection("quizzes")
            .snapshots()
            .map { snapshot ->
                val list = snapshot.toObjects(Quiz::class.java)
                if (list.isEmpty()) InitialDataSeed.quizzes else list
            }
            .catch { e ->
                handleFirestoreError(e as Exception, OperationType.LIST, "quizzes")
                emit(InitialDataSeed.quizzes)
            }
    }

    fun observeAllQuizQuestions(): Flow<List<QuizQuestion>> {
        return db.collection("quizQuestions")
            .snapshots()
            .map { snapshot ->
                val list = snapshot.toObjects(QuizQuestion::class.java)
                if (list.isEmpty()) InitialDataSeed.quizQuestions else list
            }
            .catch { e ->
                handleFirestoreError(e as Exception, OperationType.LIST, "quizQuestions")
                emit(InitialDataSeed.quizQuestions)
            }
    }

    suspend fun saveQuiz(quiz: Quiz): String {
        return try {
            val quizId = if (quiz.id.isBlank()) db.collection("quizzes").document().id else quiz.id
            val finalQuiz = quiz.copy(id = quizId)
            db.collection("quizzes").document(quizId).set(finalQuiz).await()
            quizId
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, "quizzes/${quiz.id}")
            throw e
        }
    }

    suspend fun deleteQuiz(quizId: String) {
        try {
            db.collection("quizzes").document(quizId).delete().await()
            // Delete all associated questions
            val qs = db.collection("quizQuestions").whereEqualTo("quizId", quizId).get().await()
            for (doc in qs.documents) {
                doc.reference.delete().await()
            }
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, "quizzes/$quizId")
            throw e
        }
    }

    suspend fun saveQuizQuestion(question: QuizQuestion): String {
        return try {
            val qId = if (question.id.isBlank()) db.collection("quizQuestions").document().id else question.id
            val finalQ = question.copy(id = qId)
            db.collection("quizQuestions").document(qId).set(finalQ).await()

            // Update parent quiz questionCount
            if (question.quizId.isNotBlank()) {
                val countSnapshot = db.collection("quizQuestions")
                    .whereEqualTo("quizId", question.quizId)
                    .get()
                    .await()
                db.collection("quizzes").document(question.quizId)
                    .update("questionCount", countSnapshot.size())
                    .await()
            }
            qId
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, "quizQuestions/${question.id}")
            throw e
        }
    }

    suspend fun deleteQuizQuestion(question: QuizQuestion) {
        try {
            db.collection("quizQuestions").document(question.id).delete().await()
            if (question.quizId.isNotBlank()) {
                val countSnapshot = db.collection("quizQuestions")
                    .whereEqualTo("quizId", question.quizId)
                    .get()
                    .await()
                db.collection("quizzes").document(question.quizId)
                    .update("questionCount", countSnapshot.size())
                    .await()
            }
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, "quizQuestions/${question.id}")
            throw e
        }
    }

    suspend fun syncInitialQuizzesToFirestore() {
        try {
            for (quiz in InitialDataSeed.quizzes) {
                db.collection("quizzes").document(quiz.id).set(quiz).await()
            }
            for (q in InitialDataSeed.quizQuestions) {
                db.collection("quizQuestions").document(q.id).set(q).await()
            }
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, "quizzes_sync")
        }
    }
}
