package com.example.aulalink.views

import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.aulalink.data.models.Curso
import com.example.aulalink.data.models.Documento
import com.example.aulalink.models.UserProfile

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.Timestamp

@Composable
fun CourseDetailScreenNavWrapper(
    cursoId: String,
    user: UserProfile,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()
    var documentos by remember { mutableStateOf(listOf<Documento>()) }
    var isUploading by remember { mutableStateOf(false) }

    LaunchedEffect(cursoId) {
        db.collection("cursos")
            .document(cursoId)
            .collection("documentos")
            .orderBy("createdAt")
            .addSnapshotListener { snapshot, _ ->
                documentos = snapshot?.documents?.map { doc ->
                    Documento(
                        id = doc.id,
                        titulo = doc.getString("titulo") ?: "",
                        descripcion = doc.getString("descripcion") ?: ""
                    )
                } ?: emptyList()
            }
    }

    fun subirPdf(titulo: String, pdfUri: Uri) {
        val resolver = context.contentResolver
        resolver.openInputStream(pdfUri)?.use {
            isUploading = true
            val pdfRef = storage.reference.child("pdfs/$cursoId/${System.currentTimeMillis()}.pdf")
            val uploadTask = pdfRef.putFile(pdfUri)
            uploadTask.addOnSuccessListener {
                pdfRef.downloadUrl.addOnSuccessListener { url ->
                    val docData = hashMapOf<String, Any>(
                        "titulo" to titulo,
                        "descripcion" to url.toString(),
                        "autorUid" to user.email,
                        "createdAt" to Timestamp.now()
                    )
                    db.collection("cursos")
                        .document(cursoId)
                        .collection("documentos")
                        .add(docData)
                    isUploading = false
                }
            }.addOnFailureListener {
                isUploading = false
                // Aquí puedes manejar el error, mostrar un Toast, etc.
            }
        } ?: run {
            // El archivo ya no es accesible
            Toast.makeText(context, "El archivo seleccionado ya no existe o no está accesible. Selecciónalo de nuevo.", Toast.LENGTH_LONG).show()
        }
    }

    fun eliminarDocumento(doc: Documento) {
        db.collection("cursos")
            .document(cursoId)
            .collection("documentos")
            .document(doc.id)
            .delete()
    }

    fun actualizarTitulo(doc: Documento, nuevoTitulo: String) {
        db.collection("cursos")
            .document(cursoId)
            .collection("documentos")
            .document(doc.id)
            .update("titulo", nuevoTitulo)
    }

    CourseDetailScreen(
        curso = Curso(
            id = cursoId,
            nombre = cursoId,
            descripcion = ""
        ),
        esTutor = user.role == "tutor",
        documentos = documentos,
        onAgregarDocumento = { titulo, pdfUri -> subirPdf(titulo, pdfUri) },
        onDeleteDocumento = { doc -> eliminarDocumento(doc) },
        onEditDocumento = { doc, nuevoTitulo -> actualizarTitulo(doc, nuevoTitulo) },
        onBack = onBack,
        isUploading = isUploading
    )
}
