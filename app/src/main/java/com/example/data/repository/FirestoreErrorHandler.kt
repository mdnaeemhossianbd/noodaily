package com.example.data.repository

import android.util.Log

enum class OperationType {
    GET, LIST, CREATE, UPDATE, DELETE
}

fun handleFirestoreError(exception: Exception, operation: OperationType, path: String) {
    Log.e(
        "FirestoreError",
        "Firestore error during $operation at path '$path': ${exception.localizedMessage}",
        exception
    )
}
