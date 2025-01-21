package com.image.crop

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

class CropImageView(
    context: Context, attrs: AttributeSet
) : View(context, attrs) {
    private var bitmap: Bitmap? = null

    private val cropRect = Rect()
    private val cropPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 5F
        isAntiAlias = true
    }
    private val cropCornersPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 15F
        isAntiAlias = true
    }
    private val imageRect = Rect()
    private val imagePaint = Paint()
    private var touchType: TouchType? = null

    private var layoutWidth = 0
    private var layoutHeight = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        layoutWidth = MeasureSpec.getSize(widthMeasureSpec)
        layoutHeight = MeasureSpec.getSize(heightMeasureSpec)

        setMeasuredDimension(layoutWidth, layoutHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        bitmap?.let {
            canvas?.drawBitmap(it, null, imageRect, imagePaint)
            drawCropLines(canvas)
            drawCropCorners(canvas)
        }
    }

    private fun drawCropLines(canvas: Canvas?) {
        canvas?.drawRect(cropRect, cropPaint)
    }

    private fun drawCropCorners(canvas: Canvas?) {
        // Top left
        canvas?.drawLine(
            cropRect.left.toFloat() - 5F,
            cropRect.top.toFloat() - 5F,
            cropRect.left.toFloat() - 5F + SIZE_OF_CORNER_LINE,
            cropRect.top.toFloat() - 5F,
            cropCornersPaint
        )
        canvas?.drawLine(
            cropRect.left.toFloat() - 5F,
            cropRect.top.toFloat() - 5F,
            cropRect.left.toFloat() - 5F,
            cropRect.top.toFloat() - 5F + SIZE_OF_CORNER_LINE,
            cropCornersPaint
        )

        // Top right
        canvas?.drawLine(
            cropRect.right.toFloat() + 5F - SIZE_OF_CORNER_LINE,
            cropRect.top.toFloat() - 5F,
            cropRect.right.toFloat() + 5F,
            cropRect.top.toFloat() - 5F,
            cropCornersPaint
        )
        canvas?.drawLine(
            cropRect.right.toFloat() + 5F,
            cropRect.top.toFloat() - 5F,
            cropRect.right.toFloat() + 5F,
            cropRect.top.toFloat() - 5F + SIZE_OF_CORNER_LINE,
            cropCornersPaint
        )

        // Bottom left
        canvas?.drawLine(
            cropRect.left.toFloat() - 5F,
            cropRect.bottom.toFloat() + 5F,
            cropRect.left.toFloat() - 5F + SIZE_OF_CORNER_LINE,
            cropRect.bottom.toFloat() + 5F,
            cropCornersPaint
        )
        canvas?.drawLine(
            cropRect.left.toFloat() - 5F,
            cropRect.bottom.toFloat() + 5F,
            cropRect.left.toFloat() - 5F,
            cropRect.bottom.toFloat() + 5F - SIZE_OF_CORNER_LINE,
            cropCornersPaint
        )

        // Bottom right
        canvas?.drawLine(
            cropRect.right.toFloat() - 5F - SIZE_OF_CORNER_LINE,
            cropRect.bottom.toFloat() + 5F,
            cropRect.right.toFloat() + 5F,
            cropRect.bottom.toFloat() + 5F,
            cropCornersPaint
        )
        canvas?.drawLine(
            cropRect.right.toFloat() + 5F,
            cropRect.bottom.toFloat() + 5F,
            cropRect.right.toFloat() + 5F,
            cropRect.bottom.toFloat() + 5F - SIZE_OF_CORNER_LINE,
            cropCornersPaint
        )
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                actionDownCropRect(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                actionMoveCropRect(event.x, event.y)

                invalidate()
            }
        }

        return true
    }

    private fun actionDownCropRect(x: Float, y: Float) {
        // 1. Four lines
        // 2. Four corners
        touchType = if ((x > cropRect.left + TOUCH_RADIUS && x < cropRect.right - TOUCH_RADIUS) && (y > cropRect.top - TOUCH_RADIUS && y < cropRect.top + TOUCH_RADIUS)) {
            TouchType.TOP
        } else if ((x > cropRect.left - TOUCH_RADIUS && x < cropRect.left + TOUCH_RADIUS) && (y > cropRect.top + TOUCH_RADIUS && y < cropRect.bottom - TOUCH_RADIUS)) {
            TouchType.LEFT
        } else if ((x > cropRect.right - TOUCH_RADIUS && x < cropRect.right + TOUCH_RADIUS) && (y > cropRect.top + TOUCH_RADIUS && y < cropRect.bottom - TOUCH_RADIUS)) {
            TouchType.RIGHT
        } else if ((x > cropRect.left + TOUCH_RADIUS && x < cropRect.right - TOUCH_RADIUS) && (y > cropRect.bottom - TOUCH_RADIUS && y < cropRect.bottom + TOUCH_RADIUS)) {
            TouchType.BOTTOM
        } else if ((x < cropRect.left + TOUCH_RADIUS && x > cropRect.left - TOUCH_RADIUS) && (y < cropRect.top + TOUCH_RADIUS && y > cropRect.top - TOUCH_RADIUS)) {
            TouchType.TOP_LEFT
        } else if ((x < cropRect.right + TOUCH_RADIUS && x > cropRect.right - TOUCH_RADIUS) && (y < cropRect.top + TOUCH_RADIUS && y > cropRect.top - TOUCH_RADIUS)) {
            TouchType.TOP_RIGHT
        } else if ((x < cropRect.left + TOUCH_RADIUS && x > cropRect.left - TOUCH_RADIUS) && (y < cropRect.bottom + TOUCH_RADIUS && y > cropRect.bottom - TOUCH_RADIUS)) {
            TouchType.BOTTOM_LEFT
        } else if ((x < cropRect.right + TOUCH_RADIUS && x > cropRect.right - TOUCH_RADIUS) && (y < cropRect.bottom + TOUCH_RADIUS && y > cropRect.bottom - TOUCH_RADIUS)) {
            TouchType.BOTTOM_RIGHT
        } else {
            null
        }
    }

    private fun actionMoveCropRect(x: Float, y: Float) {
        when (touchType) {
            TouchType.TOP -> {
                cropRect.apply {
                    top = y.toInt()
                }
            }
            TouchType.LEFT -> {
                cropRect.apply {
                    left = x.toInt()
                }
            }
            TouchType.RIGHT -> {
                cropRect.apply {
                    right = x.toInt()
                }
            }
            TouchType.BOTTOM -> {
                cropRect.apply {
                    bottom = y.toInt()
                }
            }
            TouchType.TOP_LEFT -> {
                cropRect.apply {
                    left = x.toInt()
                    top = y.toInt()
                }
            }
            TouchType.TOP_RIGHT -> {
                cropRect.apply {
                    top = y.toInt()
                    right = x.toInt()
                }
            }
            TouchType.BOTTOM_LEFT -> {
                cropRect.apply {
                    left = x.toInt()
                    bottom = y.toInt()
                }
            }
            TouchType.BOTTOM_RIGHT -> {
                cropRect.apply {
                    right = x.toInt()
                    bottom = y.toInt()
                }
            }
            else -> {
                Log.d(TAG, "Out of bound touch radius")
            }
        }
    }

    private fun initializeRect() {
        setRect(imageRect)
        setRect(cropRect)
    }

    private fun setRect(rect: Rect) {
        if (bitmap == null) {
            return
        }

        rect.set(
            PADDING_FORM_SCREEN,
            PADDING_FORM_SCREEN,
            bitmap!!.width - PADDING_FORM_SCREEN,
            bitmap!!.height
        )
    }

    fun setImage(pickImageBitmap: Bitmap?) {
        bitmap = pickImageBitmap

        initializeRect()
    }

    fun cropImage() {
        bitmap = Bitmap.createBitmap(bitmap!!, cropRect.left, cropRect.top, cropRect.width(), cropRect.height())

        initializeRect()

        invalidate()
    }

    companion object {
        private const val TAG = "CropImageView"
        private const val PADDING_FORM_SCREEN = 50
        private const val SIZE_OF_CORNER_LINE = 50
        private const val TOUCH_RADIUS = 50
    }
}