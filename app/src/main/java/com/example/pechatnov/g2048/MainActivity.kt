package com.example.pechatnov.g2048

import android.os.Bundle
import android.support.v7.app.AppCompatActivity;
import android.animation.ObjectAnimator
import android.view.ViewGroup.LayoutParams

// canvas
import kotlinx.android.synthetic.main.activity_main.*

import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.play_grid.*
import android.widget.LinearLayout
import android.widget.TextView
import android.util.Log
import android.graphics.Rect

import android.animation.Animator
import android.graphics.Typeface
import android.view.*


import android.content.res.Configuration
import android.graphics.drawable.GradientDrawable
import android.view.View;


class MainActivity : AppCompatActivity() {


    val CELL_COLORS = arrayOf(
        R.color.color_0,
        R.color.color_1,
        R.color.color_2,
        R.color.color_4,
        R.color.color_8,
        R.color.color_16,
        R.color.color_32,
        R.color.color_64,
        R.color.color_128,
        R.color.color_256,
        R.color.color_1024,
        R.color.color_2048
    )


    val width: Int = 5
    val height: Int = 5
    val swipeDuration: Long = 200

    var playGrid: Array<Array<LinearLayout>>? = null
    val logicPlayGridSavedStateKey = "logicPlayGrid"
    var logicPlayGrid: PlayGrid? = null
    val previousStatesSavedStateKey = "previousStates"
    val previousStates = ParcelableMutableList<PlayGrid.State>()
    val cellMap = mutableMapOf<Int, Pair<Pair<Int, Int>, View>>()

    override fun onSaveInstanceState(state: Bundle) {
        super.onSaveInstanceState(state)
        val logicPlayGrid = this.logicPlayGrid
        if (logicPlayGrid != null) {
            state.putParcelable(logicPlayGridSavedStateKey, logicPlayGrid)
            state.putParcelable(previousStatesSavedStateKey, previousStates)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        if (savedInstanceState == null) {
            Log.e("anima", "Create anew")
            this.logicPlayGrid = PlayGrid(width, height)
        } else {
            Log.e("anima", "Create load old :)")
            this.logicPlayGrid = savedInstanceState.getParcelable(logicPlayGridSavedStateKey)
            this.previousStates.addAll(savedInstanceState.getParcelable(previousStatesSavedStateKey) as ParcelableMutableList<PlayGrid.State>)
        }

        playGridLayout.removeAllViews()

        val rowsHolderLayout = LinearLayout(this)
        rowsHolderLayout.orientation = LinearLayout.VERTICAL
        rowsHolderLayout.layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT)
        rowsHolderLayout.weightSum = logicPlayGrid!!.state.height.toFloat()

        val playGridList = mutableListOf<MutableList<LinearLayout>>()

        for(i in 0 until logicPlayGrid!!.state.height) {
            val playGridRow = mutableListOf<LinearLayout>()
            val row = LinearLayout(this)
            row.orientation = LinearLayout.HORIZONTAL
            row.layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT, 1f)
            row.weightSum = logicPlayGrid!!.state.width.toFloat()
            for (j in 0 until logicPlayGrid!!.state.width) {
                val item = LinearLayout(this)
                item.orientation = LinearLayout.HORIZONTAL
                val layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT,1f)
                Log.e("f", "margin=%f".format(resources.getDimension(R.dimen.cell_margin)))
                val margin: Int = resources.getDimension(R.dimen.cell_margin).toInt()
                layoutParams.setMargins(margin, margin, margin, margin)
                item.layoutParams = layoutParams
                item.background = resources.getDrawable(R.drawable.n_0)
                row.addView(item)
                playGridRow.add(item)
            }
            rowsHolderLayout.addView(row)
            playGridList.add(playGridRow)
        }
        playGridLayout.addView(rowsHolderLayout)

        playGrid = Array<Array<LinearLayout>>(playGridList.size){playGridList[it].toTypedArray()}

        recreateCells()
        updateScore()

        playGridLayout!!.setOnTouchListener(object: OnSwipeTouchListener(this) {
            override fun onSwipe(dir: Int) {
                onGridSwipe(dir)
            }
        });

        buttonRestart.setOnClickListener {
            this.logicPlayGrid = PlayGrid(width, height)
            recreate()
        }

        buttonUndo.setOnClickListener {
            if (previousStates.size > 0) {
                val lastIndex = previousStates.size - 1
                val prevState = previousStates[lastIndex]
                previousStates.removeAt(lastIndex)
                logicPlayGrid = PlayGrid(prevState)
                recreate()
//                Cells()
//                this.constraintPlayGridLayout.invalidate()
//                updateScore()
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig);
        Log.e("anima", "ori ${newConfig.orientation} ${requestedOrientation}")
        //playGridLayout.rotation = ((newConfig.orientation + 1) * 90).toFloat()
        playGridLayout!!.invalidate()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    fun createCell(value: Int, parent: View? = null): View {
        val playGrid = this.playGrid!!
        val cellView = TextView(this)
        cellView.text = PlayGrid.valueToCost(value).toString()
        cellView.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM)
        cellView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        cellView.gravity = TextView.TEXT_ALIGNMENT_CENTER
        cellView.gravity = Gravity.CENTER
        cellView.typeface = Typeface.create("sans-serif-medium", Typeface.BOLD)

        val background = GradientDrawable()
        background.setColor(resources.getColor(CELL_COLORS[value]))
        background.cornerRadii = FloatArray(8){ resources.getDimension(R.dimen.cell_round_radius) }
        background.shape = GradientDrawable.RECTANGLE
        background.setStroke(0, 0)
        cellView.background = background

        val layoutParams = if (parent == null) {
            LinearLayout.LayoutParams(playGrid[0][0].width, playGrid[0][0].height)
        } else {
            LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        }
        layoutParams.setMargins(0, 0, 0, 0)
        cellView.setPadding(0, 0, 0, 0)
        cellView.layoutParams = layoutParams

        if (parent == null) {
            cellView.x = playGrid[0][1].x
            cellView.y = playGrid[0][1].y
        }

        return cellView
    }

    fun ejectSavingSizeAndPos(item: View, group: ViewGroup, root: ViewGroup) {
        item.layoutParams = LayoutParams(item.measuredWidth, item.measuredHeight)
        group.removeView(item)
        root.addView(item)
        copyPosition(root, item, group)
        root.invalidate()
    }

    fun ejectAll() {
        val playGrid = this.playGrid!!
        val logicPlayGrid = this.logicPlayGrid!!
        val playGridLayout = this.playGridLayout!!
        val state = logicPlayGrid.state


        for ((cId, cell) in cellMap) {

            val oldCell = cellMap[cId]!!
            val cellView = cell.second
            val (i, j) = oldCell.first
            if (cellView.parent != playGridLayout) {
                ejectSavingSizeAndPos(cellView, playGrid[i][j], playGridLayout)
                //playGridLayout.addView(cellView)
            }


        }
    }

    fun recreateCells(doInvalidate: Boolean = true) {
        Log.e("anima", "Start recreate cells $doInvalidate")
        val playGrid = this.playGrid!!
        val logicPlayGrid = this.logicPlayGrid!!
        val playGridLayout = this.playGridLayout!!
        val state = logicPlayGrid.state
        for ((cId, cell) in cellMap) {
            if (cell.second.parent == playGridLayout) {
                playGridLayout.removeView(cell.second)
            } else {
                val (i, j) = cell.first
                playGrid[i][j].removeView(cell.second)
            }
        }
        cellMap.clear()
        for (i in 0 until width) {
            for (j in 0 until height) {
                val cId = state.matrix[i][j]
                if (cId == 0) {
                    continue
                }
                Log.e("!!!", "cell %d %d".format(i, j))
                val cellView = createCell(state.values[cId]!!, playGrid[i][j])
                playGrid[i][j].addView(cellView)
                cellMap[cId] = Pair(Pair(i, j), cellView)
                copyPosition(
                    playGridLayout,
                    cellView,
                    playGrid[i][j],
                    0
                )
            }
        }
        if (doInvalidate) {
            playGridLayout.invalidate()
        }
    }

    fun updateScore() {
        scoreView.text = "Score: ${logicPlayGrid!!.state.score}"
    }

    fun onGridSwipe(dir: Int) {
        previousStates.add(logicPlayGrid!!.state)
        applyCellMove(logicPlayGrid!!.swipe(dir))
    }

    fun applyCellMove(moves: MutableMap<Int, Pair<Pair<Int, Int>, Boolean>>) {
        val playGrid = this.playGrid!!
        val logicPlayGrid = this.logicPlayGrid!!
        val playGridLayout = this.playGridLayout!!
        val state = logicPlayGrid.state

        var longAnimations = 0

        val hiddenCells = mutableListOf<View>()

        val cellMapCopy = cellMap.toMap()

        for ((cId, cell) in moves) {
            if (cId in cellMap) {
                val oldCell = cellMap[cId]!!
                val cellView = oldCell.second
                val (i, j) = oldCell.first
                if (cellView.parent != playGridLayout) {
                    ejectSavingSizeAndPos(cellView, playGrid[i][j], playGridLayout)
                    //playGridLayout.addView(cellView)
                }

                longAnimations += 1
            } else {
                val (i, j) = cell.first
                val cellView = createCell(state.values[cId]!!, playGrid[i][j])
                cellView.visibility = View.INVISIBLE
                playGrid[i][j].addView(cellView)
                cellMap[cId] = Pair(cell.first, cellView)
                hiddenCells.add(cellView)
            }
        }

        val finalAnimationsListener = object: FinalEndListener(2 * longAnimations + 1) {
            override fun onAnimationFinalEnd() {
                Log.e("anima", "Animation end!")
                for ((cId, cell) in moves) {
                    if (cId in cellMap) {
                        val cellView = cellMap[cId]!!.second
                        if (cell.second) {
                            playGridLayout.removeView(cellView)
                            cellMap.remove(cId)
                        }
                    } else {
//                        val (i, j) = cell.first
//                        val cellView = createCell(state.values[cId]!!, playGrid[i][j])
//                        playGrid[i][j].addView(cellView)
//                        cellMap[cId] = Pair(cell.first, cellView)
                    }
                }
                for (cell in hiddenCells) {
                    cell.visibility = View.VISIBLE
                }
//                recreateCells()

                updateScore()
                playGridLayout.invalidate()
            }
        }

        for ((cId, cell) in moves) {
            if (cId in cellMapCopy) {
                val cellView = cellMap[cId]!!.second
                val (i, j) = cell.first
                copyPosition(
                    playGridLayout, cellView, playGrid[i][j],
                    swipeDuration, finalAnimationsListener
                )
                Log.e("anima", "animate cId=$cId with val=${logicPlayGrid.state.values[cId]} on pos $i $j")
            }
        }

        finalAnimationsListener.onAnimationEnd(null)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        ejectAll()
        playGridLayout!!.invalidate()

//        mBoardState = (mBoardState + 1) % 2;
//        if (mBoardState == 1) {
//            recreateCells()
//        } else {
//            logicPlayGrid!!.swipe(1)
//            recreateCells()
//        }

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        @JvmStatic
        fun copyPosition(root: ViewGroup, srcChild: View, dstChild: View, durationMs: Long = 0,
                         listener: Animator.AnimatorListener? = null) {
            val rect = Rect()
            root.offsetDescendantRectToMyCoords(dstChild, rect)
            Log.e("anima", "Copy from ${srcChild.x}, ${srcChild.y} to ${rect.left}, ${rect.top}")
            if (durationMs == 0L) {
                srcChild.x = rect.left.toFloat()
                srcChild.y = rect.top.toFloat()
                return
            }
            ObjectAnimator.ofFloat(srcChild, "x", rect.left.toFloat()).apply {
                duration = durationMs
                start()
                if (listener != null) {
                    addListener(listener)
                }
            }
            ObjectAnimator.ofFloat(srcChild, "y", rect.top.toFloat()).apply {
                duration = durationMs
                start()
                if (listener != null) {
                    addListener(listener)
                }
            }
        }
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }


}
