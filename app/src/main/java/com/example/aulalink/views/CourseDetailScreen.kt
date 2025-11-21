package com.example.aulalink.views

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aulalink.data.models.Curso
import com.example.aulalink.data.models.Documento
import com.example.aulalink.views.DocumentoCard

@Composable
fun CourseDetailScreen(
    curso: Curso,
    esTutor: Boolean,
    documentos: List<Documento>,
    onAgregarDocumento: (String, Uri) -> Unit,
    onDeleteDocumento: (Documento) -> Unit,
    onEditDocumento: (Documento, String) -> Unit,
    onBack: () -> Unit,
    isUploading: Boolean = false
) {
    val context = LocalContext.current
    var tituloDoc by remember { mutableStateOf("") }
    var selectedPdfUri by remember { mutableStateOf<Uri?>(null) }
    var editMode by remember { mutableStateOf<Documento?>(null) }
    var nuevoTitulo by remember { mutableStateOf("") }

    val pickPdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedPdfUri = uri
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1A1A1A), Color(0xFF222242))
                )
            )
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 18.dp)
        ) {
            TextButton(onClick = onBack) {
                Text("← Volver", color = Color.Red)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = curso.nombre,
                color = Color.White,
                fontSize = 22.sp
            )
        }

        if (esTutor) {
            Text(
                text = "Subir nuevo PDF:",
                color = Color.White,
                fontSize = 15.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = tituloDoc,
                onValueChange = { tituloDoc = it },
                label = { Text("Título del PDF") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )
            Row(modifier = Modifier.padding(bottom = 8.dp)) {
                Button(
                    onClick = { pickPdfLauncher.launch("application/pdf") },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("Seleccionar PDF")
                }
                if (selectedPdfUri != null) {
                    Text("PDF listo", color = Color.Red)
                }
            }
            Button(
                onClick = {
                    if (tituloDoc.isNotBlank() && selectedPdfUri != null) {
                        onAgregarDocumento(tituloDoc.trim(), selectedPdfUri!!)
                        tituloDoc = ""
                        selectedPdfUri = null
                    }
                },
                enabled = tituloDoc.isNotBlank() && selectedPdfUri != null && !isUploading,
                modifier = Modifier.fillMaxWidth().padding(bottom = 18.dp)
            ) {
                if (isUploading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Text("Subir PDF")
                }
            }
        }

        Text(
            text = "Documentos:",
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(documentos) { doc ->
                if (editMode == doc) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = nuevoTitulo,
                            onValueChange = { nuevoTitulo = it },
                            label = { Text("Editar título") }
                        )
                        Button(
                            onClick = {
                                onEditDocumento(doc, nuevoTitulo)
                                editMode = null
                            },
                            enabled = nuevoTitulo.isNotBlank()
                        ) { Text("Guardar") }
                        TextButton(onClick = { editMode = null }) { Text("Cancelar") }
                    }
                } else {
                    DocumentoCard(
                        doc = doc,
                        esTutor = esTutor,
                        onDelete = { onDeleteDocumento(doc) },
                        onEdit = {
                            editMode = doc
                            nuevoTitulo = doc.titulo
                        },
                        onOpenPdf = { url -> openPdfUrl(url, context) }
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

fun openPdfUrl(url: String, context: android.content.Context) {
    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
        setDataAndType(android.net.Uri.parse(url), "application/pdf")
        flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(intent)
}
