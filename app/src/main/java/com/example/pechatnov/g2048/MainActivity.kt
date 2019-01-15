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
import android.widget.Toast
import android.os.StrictMode
import android.support.v7.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent


class MainActivity : ActivityWithSettings() {

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


    var width: Int = 5
    val widthSavedStateKey = "width"
    var height: Int = 5
    val heightSavedStateKey = "height"
    var swipeDuration: Long = 200
    val swipeDurationSavedStateKey = "swipeDuration"
    var newBlockPolicy: SettingsKeeper.BlockStrategy = SettingsKeeper.BlockStrategy.CENTER
    val newBlockPolicySavedStateKey = "newBlockPolicy"

    var playGrid: Array<Array<LinearLayout>>? = null
    val logicPlayGridSavedStateKey = "logicPlayGrid"
    var logicPlayGrid: PlayGrid? = null
    val previousStatesSavedStateKey = "previousStates"
    val previousStates = ParcelableMutableList<PlayGrid.State>()
    val cellMap = mutableMapOf<Int, Pair<Pair<Int, Int>, View>>()

    fun setPlayGridSize(size: Int) {
        if (size != this.width) {
            this.width = size
            this.height = size
            this.logicPlayGrid = PlayGrid(width, height, newBlockPolicy)
            this.previousStates.clear()
            recreate()
        }
    }

    fun setBlockStrategy(policy: SettingsKeeper.BlockStrategy) {
        if (policy != this.newBlockPolicy) {
            this.logicPlayGrid = PlayGrid(width, height, policy)
            this.newBlockPolicy = policy
            recreate()
        }
    }


    override fun onSaveInstanceState(state: Bundle) {
        Log.e("place", "onSaveInstanceState ${previousStates.size}")
        super.onSaveInstanceState(state)
        val logicPlayGrid = this.logicPlayGrid
        if (logicPlayGrid != null) {
            state.putParcelable(logicPlayGridSavedStateKey, logicPlayGrid)
            state.putParcelable(previousStatesSavedStateKey, previousStates)
        }
        state.putInt(widthSavedStateKey, width)
        state.putInt(heightSavedStateKey, height)
        state.putLong(swipeDurationSavedStateKey, swipeDuration)
        state.putInt(newBlockPolicySavedStateKey, newBlockPolicy.value)
    }

    override fun onResume() {
        Log.e("place", "onResume")
        val settings = SettingsKeeper(this)
        setPlayGridSize(settings.fieldSize.toInt())
        swipeDuration = settings.swipeSpeed.toLong()
        setBlockStrategy(settings.blockStrategy)
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        //recreate()
    }

    override fun onStart() {
        Log.e("place", "onStart")
        //recreateCells()
        //updateScore()
        super.onStart()
    }

    override fun onRestart() {
        Log.e("place", "onRestart")
        super.onRestart()
        recreate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e("place", "onCreate")
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        if (savedInstanceState == null) {
            Log.e("anima", "Create anew")
            this.logicPlayGrid = PlayGrid(width, height, this.newBlockPolicy)
        } else {
            Log.e("anima", "Create load old :)")
            this.width = savedInstanceState.getInt(widthSavedStateKey)
            this.height = savedInstanceState.getInt(heightSavedStateKey)
            this.swipeDuration = savedInstanceState.getLong(swipeDurationSavedStateKey)
            this.newBlockPolicy = SettingsKeeper.BlockStrategy.fromInt(savedInstanceState.getInt(newBlockPolicySavedStateKey))!!
            this.logicPlayGrid = savedInstanceState.getParcelable(logicPlayGridSavedStateKey)
            this.previousStates.addAll(savedInstanceState.getParcelable(previousStatesSavedStateKey) as ParcelableMutableList<PlayGrid.State>)
        }

        Log.e("place", "onCreate _cont ${previousStates.size}")

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
            doRestart()
        }

        buttonUndo.setOnClickListener {
            if (previousStates.size > 0) {
                val lastIndex = previousStates.size - 1
                val prevState = previousStates[lastIndex]
                previousStates.removeAt(lastIndex)
                logicPlayGrid = PlayGrid(prevState, newBlockPolicy)
                recreate()
            }
        }
    }

    fun doRestart() {
        this.logicPlayGrid = PlayGrid(width, height, newBlockPolicy)
        this.previousStates.clear()
        recreate()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig);
        Log.e("anima", "ori ${newConfig.orientation} ${requestedOrientation}")
        playGridLayout!!.invalidate()
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
        playScore = logicPlayGrid!!.state.score
        scoreView.text = "${resources.getString(R.string.score_text)} ${playScore}"
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

        val context = this
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
                    }
                }
                for (cell in hiddenCells) {
                    cell.visibility = View.VISIBLE
                }
                updateScore()
                playGridLayout.invalidate()

                if (logicPlayGrid.isGameOver()) {
                    onGameOver()
                }
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

    fun onGameOver() {

        val context = this
        val builder = AlertDialog.Builder(this);
        builder.setTitle(R.string.add_score_msg)
        builder.setMessage(R.string.add_score_to_rating)
        builder.setNegativeButton(R.string.add_score_add, object: DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, id: Int) {
                val startRatingActivityIntent = Intent(context, RatingActivity::class.java)
                startRatingActivityIntent.putExtra("score", playScore)
                startActivity(startRatingActivityIntent)
                doRestart()
            }
        });
        builder.setPositiveButton(R.string.add_score_cancel, object: DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, id: Int) {
                doRestart()
            }
        });
        builder.show()
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
