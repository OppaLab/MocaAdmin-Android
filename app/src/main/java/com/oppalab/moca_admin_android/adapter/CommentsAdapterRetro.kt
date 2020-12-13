package com.oppalab.moca_admin_android.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.oppalab.moca_admin_android.R
import com.oppalab.moca_admin_android.dto.CommentsOnPost
import com.oppalab.moca.util.PreferenceManager
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CommentsAdapterRetro(private val mContext: Context,
                           private val mComment: MutableList<CommentsOnPost>?

) : RecyclerView.Adapter<CommentsAdapterRetro.ViewHolder>() {

    private val viewPool = RecyclerView.RecycledViewPool()

    private val currentUser = PreferenceManager.getLong(mContext, "userId")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsAdapterRetro.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.comments_item_layout, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("WrongConstant")
    override fun onBindViewHolder(holder: CommentsAdapterRetro.ViewHolder, position: Int) {

        val comment = mComment!![position]
        holder.commentTV.text = comment.comment
        holder.userNameTV.text = comment.nickname
        holder.imageProfile.setImageResource(R.drawable.profile)

    }

    override fun getItemCount(): Int {
        return mComment!!.size
    }

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView){
        var imageProfile: CircleImageView
        var userNameTV: TextView
        var commentTV: TextView

        init {
            imageProfile = itemView.findViewById(R.id.user_profile_image_comment)
            userNameTV = itemView.findViewById(R.id.nickname_comment)
            commentTV = itemView.findViewById(R.id.comment_comment)
        }
    }

    interface CommentClickListener {
        fun onClick(pos:Int) {
        }
    }

    abstract class SwipeHelper(context: Context, private val recyclerView: RecyclerView, internal var buttonWidth:Int)
        :ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){

        private var buttonList:MutableList<DeleteButton>?=null
        lateinit var gestureDetector: GestureDetector
        var swipePosition=-1
        var swipeThresholds=0.5f
        val buttonBuffer:MutableMap<Int, MutableList<DeleteButton>>
        lateinit var removerQueue: LinkedList<Int>

        abstract fun instantiateDeleteButton(viewHolder: RecyclerView.ViewHolder,
                                            buffer:MutableList<DeleteButton>)

        private val gestureListener = object :GestureDetector.SimpleOnGestureListener(){
            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                for(button in buttonList!!)
                    if (button.onClick(e!!.x, e!!.y))
                        break
                return true
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        private val onTouchListener = View.OnTouchListener { _, motionEvent ->
            if (swipePosition < 0) return@OnTouchListener false
            val point = Point (motionEvent.rawX.toInt(), motionEvent.rawY.toInt())
            val swipeViewHolder = recyclerView.findViewHolderForAdapterPosition(swipePosition)
            val swipeItem = swipeViewHolder!!.itemView
            val rect = Rect()
            swipeItem.getGlobalVisibleRect(rect)

            if(motionEvent.action == MotionEvent.ACTION_DOWN || motionEvent.action == MotionEvent.ACTION_MOVE || motionEvent.action == MotionEvent.ACTION_UP) {
                if(rect.top < point.y && rect.bottom > point.y)
                    gestureDetector.onTouchEvent(motionEvent)
                else {
                    removerQueue.add(swipePosition)
                    swipePosition = -1
                    recoverSwipeItem()
                }
            }
            false
        }

        @Synchronized
        private fun recoverSwipeItem() {
            while (!removerQueue.isEmpty())
            {
                val pos = removerQueue.poll()!!.toInt()

                if (pos > -1)
                    recyclerView.adapter!!.notifyItemChanged(pos)
            }
        }

        init {
            this.buttonList = ArrayList()
            this.gestureDetector = GestureDetector(context,gestureListener)
            this.recyclerView.setOnTouchListener(onTouchListener)
            this.buttonBuffer = HashMap()
            this.removerQueue = IntLinkedList()

            attachSwipe()
        }

        private fun attachSwipe() {
            val itemTouchHelper = ItemTouchHelper(this)
            itemTouchHelper.attachToRecyclerView(recyclerView)
        }


        class IntLinkedList : LinkedList<Int>() {
            override fun contains(element: Int): Boolean {
                return false
            }

            override fun lastIndexOf(element: Int): Int {
                return element
            }

            override fun remove(element: Int): Boolean {
                return false
            }

            override fun indexOf(element: Int): Int {
                return element
            }

            override fun add(element: Int): Boolean {
                return if(contains(element))
                    false
                else super.add(element)
            }
        }


        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val pos = viewHolder.adapterPosition
            if (swipePosition != pos)
                removerQueue.add(swipePosition)
            swipePosition = pos
            if (buttonBuffer.containsKey(swipePosition))
                buttonList = buttonBuffer[swipePosition]
            else
                buttonList!!.clear()
            buttonBuffer.clear()
            swipeThresholds = 0.5f*buttonList!!.size.toFloat()*buttonWidth.toFloat()
            recoverSwipeItem()
        }

        override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
            return swipeThresholds
        }

        override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
            return 0.1f*defaultValue
        }

        override fun getSwipeVelocityThreshold(defaultValue: Float): Float {
            return 5.0f*defaultValue
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            val pos = viewHolder.adapterPosition
            var translationX = dX
            val itemView = viewHolder.itemView
            if (pos < 0) {
                swipePosition = pos
                return
            }
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
                if (dX < 0) {
                    var buffer : MutableList<DeleteButton> = ArrayList()
                    if (!buttonBuffer.containsKey(pos)){
                        instantiateDeleteButton(viewHolder,buffer)
                        buttonBuffer[pos] = buffer
                    } else {
                        buffer = buttonBuffer[pos]!!
                    }
                    translationX = dX*buffer.size.toFloat() * buttonWidth.toFloat() / itemView.width
                    drawButton(c, itemView, buffer, pos, translationX)
                }
            }
            super.onChildDraw(c, recyclerView, viewHolder, translationX, dY, actionState, isCurrentlyActive)
        }

        private fun drawButton(c: Canvas, itemView: View, buffer: MutableList<CommentsAdapterRetro.SwipeHelper.DeleteButton>, pos: Int, translationX: Float) {
            var right = itemView.right.toFloat()
            val dButtonWidth = -1*translationX/buffer.size
            for (button in buffer) {
                val left = right - dButtonWidth
                button.onDraw(c, RectF(left, itemView.top.toFloat(), right, itemView.bottom.toFloat()), pos)
                right = left
            }
        }

        class DeleteButton(private val context: Context, private val text:String,
                           private val textSize:Int , private val profileImage:Int, private var color:Int,
                           private val listener:CommentsAdapterRetro.CommentClickListener) {
            private var pos:Int=0
            private var clickRegion: RectF?=null
            private var resources:Resources

            init {
                resources = context.resources;
            }

            fun onClick(x:Float,y:Float):Boolean {
                if(clickRegion != null && clickRegion!!.contains(x,y)) {
                    listener.onClick(pos)
                    return true
                }
                return false
            }

            fun onDraw(c: Canvas, rectF:RectF, pos:Int) {
                val p = Paint()
                p.color = color
                c.drawRect(rectF,p)

                //Text
                p.color = Color.WHITE
                p.textSize = textSize.toFloat()

                val r = Rect()
                val cHeight = rectF.height()
                val cWidth = rectF.width()
                p.textAlign = Paint.Align.LEFT
                p.getTextBounds(text, 0, text.length, r)
                var x = 0f
                var y = 0f
                if (profileImage == 0) {
                    x = cWidth / 2f - r.width() / 2f - r.left.toFloat()
                    y = cHeight / 2f + r.height() / 2f - r.bottom.toFloat()
                    c.drawText(text, rectF.left+x,rectF.top+y,p)
                } else {
                    val d = ContextCompat.getDrawable(context
                        ,profileImage)
                    val bitmap = drawableToBitmap(d)
                    c.drawBitmap(bitmap, (rectF.left+rectF.right/2), (rectF.top+rectF.bottom/2), p)
                }

                clickRegion = rectF
                this.pos = pos
            }

            private fun drawableToBitmap(d: Drawable?): Bitmap {
                if(d is BitmapDrawable) return d.bitmap
                val bitmap = Bitmap.createBitmap(d!!.intrinsicWidth,d.intrinsicHeight,Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                d.setBounds(0, 0, canvas.width, canvas.height)
                d.draw(canvas)
                return bitmap
            }
        }
    }
}