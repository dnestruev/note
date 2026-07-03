package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Note
import com.example.viewmodel.NotesViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(
    viewModel: NotesViewModel,
    onAddNote: () -> Unit,
    onNoteClick: (Int) -> Unit,
    onSettings: () -> Unit,
    onLock: () -> Unit
) {
    val notes by viewModel.notes.collectAsState()

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("NETWORK: ENCRYPTED", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f))
                    Text("ACTIVE", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f))
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Light, fontSize = 36.sp)) {
                                append("Ghost")
                            }
                            withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold, fontSize = 36.sp, color = MaterialTheme.colorScheme.primary)) {
                                append("Notes")
                            }
                        }
                    )
                    
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                            .clickable(onClick = onLock),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Lock, contentDescription = "Lock", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddNote,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Note")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (notes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("No secured data found.", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(32.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(32.dp))
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                progress = { 0.98f },
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                modifier = Modifier.fillMaxSize()
                            )
                            Icon(Icons.Filled.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp).clickable(onClick = onSettings))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Security Score: 98%", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Text("E2E Encryption engaged", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("VOLATILE FEED", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
                        Box(modifier = Modifier.background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                            Text("STABLE", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                items(notes) { note ->
                    NoteItem(note = note, onClick = { onNoteClick(note.id) })
                }
                
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun NoteItem(note: Note, onClick: () -> Unit) {
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val dateString = dateFormat.format(Date(note.timestamp))
    
    val isBurn = note.isBurnAfterReading || note.expiresAt != null
    val errorColor = MaterialTheme.colorScheme.error
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = if (isBurn) Color.Transparent else Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(28.dp)
            )
            .drawBehind {
                if (isBurn) {
                    drawLine(
                        color = errorColor,
                        start = Offset(0f, 0f),
                        end = Offset(0f, size.height),
                        strokeWidth = 8.dp.toPx()
                    )
                }
            }
            .clickable(onClick = onClick)
            .padding(20.dp)
    ) {
        Column(modifier = Modifier.padding(start = if (isBurn) 8.dp else 0.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = note.title.ifEmpty { "Untitled" },
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                    )
                    if (isBurn) {
                        Text(
                            text = "SELF-DESTRUCT SEQUENCE",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    } else if (note.isHidden) {
                        Row(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(6.dp)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                                Text("ENCRYPTED", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                            }
                            Box(modifier = Modifier.background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(6.dp)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                                Text("CATEGORY: ZERO", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                            }
                        }
                    }
                }
                
                if (isBurn) {
                    Row(
                        modifier = Modifier.background(MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(12.dp)).padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(dateString, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp
            )
            
            if (isBurn) {
                Spacer(modifier = Modifier.height(20.dp))
                Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(2.dp))) {
                    Box(modifier = Modifier.fillMaxWidth(0.66f).height(4.dp).background(MaterialTheme.colorScheme.error, RoundedCornerShape(2.dp)))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FakeHomeScreen(onLock: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("NOTES", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
                actions = {
                    IconButton(onClick = onLock) {
                        Icon(Icons.Filled.Lock, contentDescription = "Lock", tint = MaterialTheme.colorScheme.onBackground)
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
            Text("No notes.", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
        }
    }
}
