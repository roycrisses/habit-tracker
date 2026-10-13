package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * High-polish iOS-style swipeable card with sticky resistance, spring physics,
 * two-sided sliding actions:
 * - Slide Right: Fluid green checkmark to Complete / Toggle
 * - Slide Left: Reveals sticky Archive (Amber) and Delete (Crimson) action drawer,
 *   or deep-drag to trigger direct dismissal.
 */
@Composable
fun SwipeableActionCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(20.dp),
    isCompleted: Boolean = false,
    onSwipeRight: (() -> Unit)? = null,
    onArchive: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    var isRemoved by remember { mutableStateOf(false) }

    // Action drawer reveal threshold (in px)
    val revealWidthDp = 150.dp
    val revealWidthPx = with(density) { revealWidthDp.toPx() }
    val deepSwipeThresholdPx = with(density) { 240.dp.toPx() }
    val rightSwipeThresholdPx = with(density) { 100.dp.toPx() }

    val offsetX = remember { Animatable(0f) }
    var rawDragOffset by remember { mutableFloatStateOf(0f) }
    val interactionSource = remember { MutableInteractionSource() }

    AnimatedVisibility(
        visible = !isRemoved,
        exit = shrinkVertically(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeOut(animationSpec = tween(220))
    ) {
        BoxWithConstraints(
            modifier = modifier
                .fillMaxWidth()
                .clip(shape)
        ) {
            val totalWidthPx = constraints.maxWidth.toFloat()

            // -------------------------------------------------------------
            // BACKGROUND ACTION LAYER (Underneath the sliding card)
            // -------------------------------------------------------------
            val currentOffset = offsetX.value

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(shape)
            ) {
                // Left-side background (Revealed when sliding RIGHT) -> COMPLETE
                if (currentOffset > 0 && onSwipeRight != null) {
                    val progress = (currentOffset / rightSwipeThresholdPx).coerceIn(0f, 1.8f)
                    val iconScale by animateFloatAsState(
                        targetValue = if (currentOffset >= rightSwipeThresholdPx) 1.25f else (0.75f + progress * 0.35f),
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "rightIconScale"
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF059669),
                                        Color(0xFF10B981),
                                        Color(0xFF34D399).copy(alpha = 0.85f)
                                    )
                                )
                            )
                            .padding(start = 22.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .scale(iconScale)
                                    .shadow(6.dp, CircleShape, spotColor = Color(0x60047857))
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Complete Habit",
                                    tint = Color(0xFF059669),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = if (isCompleted) "Undo" else "Done",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (currentOffset >= rightSwipeThresholdPx) "Release to save" else "Slide to finish",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Right-side background (Revealed when sliding LEFT) -> ARCHIVE & DELETE
                if (currentOffset < 0 && (onArchive != null || onDelete != null)) {
                    val dragFraction = (abs(currentOffset) / revealWidthPx).coerceIn(0f, 1.6f)
                    val isPastDeepSwipe = abs(currentOffset) >= deepSwipeThresholdPx

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                if (isPastDeepSwipe) {
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color(0xFFDC2626),
                                            Color(0xFFEF4444)
                                        )
                                    )
                                } else {
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color(0xFFF3F4F6),
                                            Color(0xFFE5E7EB)
                                        )
                                    )
                                }
                            ),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(end = 10.dp, top = 6.dp, bottom = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // ARCHIVE BUTTON (Amber Pill)
                            if (onArchive != null && !isPastDeepSwipe) {
                                val archiveScale = (0.7f + dragFraction * 0.35f).coerceIn(0.7f, 1.05f)
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .width(66.dp)
                                        .graphicsLayer { scaleX = archiveScale; scaleY = archiveScale }
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(Color(0xFFFBBF24), Color(0xFFD97706))
                                            )
                                        )
                                        .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            coroutineScope.launch {
                                                offsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                            }
                                            onArchive()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Archive,
                                            contentDescription = "Archive",
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Archive",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            // DELETE BUTTON (Crimson Pill)
                            if (onDelete != null) {
                                val deleteScale = (0.7f + dragFraction * 0.35f).coerceIn(0.7f, 1.15f)
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .width(if (isPastDeepSwipe) 120.dp else 66.dp)
                                        .graphicsLayer { scaleX = deleteScale; scaleY = deleteScale }
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(Color(0xFFEF4444), Color(0xFFB91C1C))
                                            )
                                        )
                                        .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            coroutineScope.launch {
                                                isRemoved = true
                                            }
                                            onDelete()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = if (isPastDeepSwipe) "Release to Delete" else "Delete",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // FOREGROUND SLIDING CARD LAYER (User's Card Content)
            // -------------------------------------------------------------
            val offsetDp = with(density) { offsetX.value.toDp() }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                    .graphicsLayer {
                        // Subtle tactile tilt and shadow depth as it slides
                        val rot = (offsetX.value / totalWidthPx) * 2.2f
                        rotationZ = rot
                    }
                    .pointerInput(isCompleted) {
                        detectHorizontalDragGestures(
                            onDragStart = {
                                rawDragOffset = offsetX.value
                            },
                            onHorizontalDrag = { _, dragAmount ->
                                rawDragOffset += dragAmount

                                // Sticky elastic damping equation
                                val target = if (rawDragOffset > 0) {
                                    // Right swipe: damping past threshold
                                    if (rawDragOffset > rightSwipeThresholdPx) {
                                        rightSwipeThresholdPx + (rawDragOffset - rightSwipeThresholdPx) * 0.32f
                                    } else rawDragOffset
                                } else {
                                    // Left swipe: damping past action drawer reveal
                                    if (rawDragOffset < -revealWidthPx) {
                                        -revealWidthPx + (rawDragOffset + revealWidthPx) * 0.36f
                                    } else rawDragOffset
                                }

                                coroutineScope.launch {
                                    offsetX.snapTo(target)
                                }
                            },
                            onDragEnd = {
                                coroutineScope.launch {
                                    val current = offsetX.value
                                    when {
                                        // Trigger right swipe action
                                        current >= rightSwipeThresholdPx && onSwipeRight != null -> {
                                            offsetX.animateTo(
                                                targetValue = 0f,
                                                animationSpec = spring(
                                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                                    stiffness = Spring.StiffnessMediumLow
                                                )
                                            )
                                            onSwipeRight()
                                        }

                                        // Deep swipe left -> trigger delete directly
                                        current <= -deepSwipeThresholdPx && onDelete != null -> {
                                            isRemoved = true
                                            onDelete()
                                        }

                                        // Partial swipe left -> Snap to sticky reveal action drawer
                                        current <= -revealWidthPx * 0.45f && (onArchive != null || onDelete != null) -> {
                                            offsetX.animateTo(
                                                targetValue = -revealWidthPx,
                                                animationSpec = spring(
                                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                                    stiffness = Spring.StiffnessMedium
                                                )
                                            )
                                        }

                                        // Otherwise spring back closed
                                        else -> {
                                            offsetX.animateTo(
                                                targetValue = 0f,
                                                animationSpec = spring(
                                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                                    stiffness = Spring.StiffnessMediumLow
                                                )
                                            )
                                        }
                                    }
                                }
                            },
                            onDragCancel = {
                                coroutineScope.launch {
                                    offsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                }
                            }
                        )
                    }
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        // Tapping on card when open snaps it closed
                        if (offsetX.value != 0f) {
                            coroutineScope.launch {
                                offsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                            }
                        }
                    }
            ) {
                content()
            }
        }
    }
}
