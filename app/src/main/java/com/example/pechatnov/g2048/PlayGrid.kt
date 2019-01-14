package com.example.pechatnov.g2048

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import java.util.ArrayList
import java.util.Random
import java.util.TreeSet

class PlayGrid(width: Int, height: Int, blockStrategy: SettingsKeeper.BlockStrategy) : Parcelable {

    class State(width: Int, height: Int) {
        val width = width;
        val height = height;
        val matrix = Array(width){Array(height){0}}
        val values: MutableMap<Int, Int> = mutableMapOf<Int, Int>()
        var score: Int = 0

        constructor(matrix: Array<Array<Int>>, values: Map<Int, Int>, score: Int = 0) : this(matrix.size, matrix[0].size) {
            init(matrix, values, score)
        }

        constructor(state: State) : this(state.matrix.size, state.matrix[0].size) {
            init(state.matrix, state.values, state.score)
        }

        fun init(matrix: Array<Array<Int>>, values: Map<Int, Int>, score: Int) {
            for ((k, v) in values) {
                this.values[k] = v
            }
            for (i in 0 until width) {
                for (j in 0 until height) {
                    this.matrix[i][j] = matrix[i][j]
                }
            }
            this.score = score
        }

        fun rotateClockwise() {
            val matrix = Array(width) { this.matrix[it].copyOf() }
            for (i in 0 until width) {
                for (j in 0 until height) {
                    this.matrix[j][width - 1 - i] = matrix[i][j]
                }
            }
        }
    }

    private var currentState = State(width, height)
    private val blockStrategy = blockStrategy

    val state: State
        get() = State(currentState.matrix, currentState.values, currentState.score)

    constructor(state: State, blockStrategy: SettingsKeeper.BlockStrategy) : this(state.width, state.height, blockStrategy) {
        currentState = state
    }

    fun isGameOver(): Boolean {
        for (dir in 0..3) {
            val playGrid = PlayGrid(State(currentState), blockStrategy)
            playGrid.swipe(dir)
            val oldState = state
            val newState = playGrid.state

            if (!oldState.values.equals(newState.values)) {
                return false
            }
        }
        return true
    }

    fun swipe(dir: Int): MutableMap<Int, Pair<Pair<Int, Int>, Boolean>> {
        assert(dir in 0..3)

        if (currentState.values.isEmpty()) {
            return mutableMapOf<Int, Pair<Pair<Int, Int>, Boolean>>()
        }

        // rotate dir times
        for (dirI in 0 until dir) {
            currentState.rotateClockwise()
        }

        var currentNo = (currentState.values.maxBy { it.key })!!.key + 1
        // cellNo -> ((dstI, dstJ), shouldDelete)
        var toMove = mutableMapOf<Int, Pair<Pair<Int, Int>, Boolean>>()

        for (i in 0 until currentState.width) {
            val cells = mutableListOf<Int>()
            var lastMerged = true
            for (j in 0 until currentState.height) {
                val lc = currentState.matrix[i][j]
                if (lc == 0) {
                    continue
                }

                cells.add(lc)

                if (!lastMerged) {
                    val llc = cells[cells.size - 2]
                    assert(lc != 0 && llc != 0)
                    if (currentState.values[lc] == currentState.values[llc]) {
                        val newValue = currentState.values[lc]!! + 1
                        currentState.score += valueToCost(newValue)
                        val newJ = cells.size - 2
                        for (c in intArrayOf(lc, llc)) {
                            cells.removeAt(cells.size - 1)
                            currentState.values.remove(c)
                            toMove[c] = Pair(Pair(i, newJ), true)
                        }
                        cells.add(currentNo)
                        currentState.values[currentNo] = newValue
                        toMove[currentNo] = Pair(Pair(i, newJ), false)
                        currentNo += 1
                        lastMerged = true
                        continue
                    }
                }
                lastMerged = false
                val newJ = cells.size - 1
                if (newJ != j) {
                    toMove[cells[newJ]] = Pair(Pair(i, newJ), false)
                }
            }
            for (j in 0 until cells.size) {
                currentState.matrix[i][j] = cells[j]
            }
            for (j in cells.size until currentState.height) {
                currentState.matrix[i][j] = 0
            }
        }

        val candidates = ArrayList<Pair<Int, Int>>();
        when (this.blockStrategy) {
            SettingsKeeper.BlockStrategy.CENTER -> {
                val hw = (currentState.width - 1) / 2
                val hh = (currentState.height - 1) / 2
                val hw1 = currentState.width / 2
                val hh1 = currentState.height / 2
                candidates.add(Pair(hw, hh))
                candidates.add(Pair(hw1, hh))
                candidates.add(Pair(hw, hh1))
                candidates.add(Pair(hw1, hh1))
            }
            SettingsKeeper.BlockStrategy.RANDOM -> {
                for (i in 0 until currentState.width) {
                    for (j in 0 until currentState.height) {
                        candidates.add(Pair(i, j))
                    }
                }
            }
            SettingsKeeper.BlockStrategy.RANDOM_CORNER -> {
                candidates.addAll(arrayOf(
                        Pair(0, 0),
                        Pair(0, currentState.height - 1),
                        Pair(currentState.width - 1, 0),
                        Pair(currentState.width - 1, currentState.height - 1)
                ))
            }
        }
        val emptyCandidates = candidates.filter { currentState.matrix[it.first][it.second] == 0 }

        if (emptyCandidates.isEmpty()) {
            // do nothing
        } else {
            val (i, j) = emptyCandidates[Random().nextInt(emptyCandidates.size)]
            currentState.matrix[i][j] = currentNo
            currentState.values[currentNo] = 1
            toMove[currentNo] = Pair(Pair(i, j), false)
            currentNo += 1
        }

        // rotate back
        for (dirI in dir until 4) {
            currentState.rotateClockwise()
            toMove = (toMove.mapValues {
                Pair(Pair(it.value.first.second, currentState.width - 1 - it.value.first.first), it.value.second)
            }).toMutableMap()
        }

        return toMove
    }

    fun fullState(): MutableMap<Int, Pair<Pair<Int, Int>, Boolean>> {
        val toMove = mutableMapOf<Int, Pair<Pair<Int, Int>, Boolean>>()
        for (i in 0 until currentState.width) {
            for (j in 0 until currentState.height) {
                val lc = currentState.matrix[i][j]
                if (lc == 0) {
                    continue
                }
                toMove[lc] = Pair(Pair(i, j), false)
            }
        }
        return toMove
    }

    init {
        assert(width == height)

        //debug
        currentState.matrix[1][1] = 1
        currentState.values[1] = 1
    }

    companion object {
        @JvmStatic
        fun valueToCost(value: Int): Int = Math.pow(2.0, value.toDouble() - 1.0).toInt()
    }


    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {}

    @JvmField
    val CREATOR: Parcelable.Creator<PlayGrid> = object : Parcelable.Creator<PlayGrid> {
        override fun createFromParcel(input: Parcel): PlayGrid {
            assert(false)
            return PlayGrid(3, 3, SettingsKeeper.BlockStrategy.CENTER)
        }
        override fun newArray(size: Int): Array<PlayGrid?> {
            return arrayOfNulls<PlayGrid>(size)
        }
    }
}