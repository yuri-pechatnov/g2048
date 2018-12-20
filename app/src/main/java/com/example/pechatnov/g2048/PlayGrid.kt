package com.example.pechatnov.g2048

import android.os.Parcel
import android.os.Parcelable
import android.util.Log

class PlayGrid(width: Int, height: Int) : Parcelable {

    class State(width: Int, height: Int) {
        val width = width;
        val height = height;
        val matrix = Array(width){Array(height){0}}
        val values: MutableMap<Int, Int> = mutableMapOf<Int, Int>()
        var score: Int = 0

        constructor(matrix: Array<Array<Int>>, values: Map<Int, Int>, score: Int = 0) : this(matrix.size, matrix[0].size) {
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

    val state: State
        get() = State(currentState.matrix, currentState.values, currentState.score)

    constructor(state: State) : this(state.width, state.height) {
        currentState = state
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

        //debug
        if (currentState.matrix[2][2] == 0) {
            currentState.matrix[2][2] = currentNo
            currentState.values[currentNo] = 1
            toMove[currentNo] = Pair(Pair(2, 2), false)
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
            return PlayGrid(3, 3)
        }
        override fun newArray(size: Int): Array<PlayGrid?> {
            return arrayOfNulls<PlayGrid>(size)
        }
    }
}