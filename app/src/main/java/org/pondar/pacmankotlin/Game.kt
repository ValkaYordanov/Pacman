package org.pondar.pacmankotlin

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.makeText
import java.lang.Math.pow
import java.lang.Math.sqrt
import java.util.ArrayList


/**
 *
 * This class should contain all your game logic
 */

class Game(private var context: Context, view: TextView) {

    private var pointsView: TextView = view
    private var points: Int = 0

    private var running = false
    var direction = 2

    //bitmap of the pacman
    var pacBitmap: Bitmap
    var pacx: Int = 0
    var pacy: Int = 0
    var count: Int = 0
    var coin: GoldCoin = GoldCoin();
    var coinBitmap: Bitmap
    val toastA = Toast.makeText(
        context,
        "You win the game!",
        Toast.LENGTH_LONG
    )

    //did we initialize the coins?
    var coinsInitialized = false

    //the list of goldcoins - initially empty
    var coins = ArrayList<GoldCoin>()

    //a reference to the gameview
    private lateinit var gameView: GameView
    private var h: Int = 0
    private var w: Int = 0 //height and width of screen


    //The init code is called when we create a new Game class.
    //it's a good place to initialize our images.
    init {
        pacBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.pacman)
        coinBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.coin)
    }

    fun setGameView(view: GameView) {
        this.gameView = view
    }

    //TODO initialize goldcoins also here
    fun initializeGoldcoins() {

        for (i in 0..2)
            coins.add(GoldCoin((0..850).random(), (0..850).random()))

        coinsInitialized = true
    }


    fun newGame() {
        pacx = 50
        pacy = 400 //just some starting coordinates - you can change this.
        //reset the points
        toastA.cancel()
        count = 0
        coinsInitialized = false
        points = 0
        pointsView.text = "${context.resources.getString(R.string.points)} $points"
        gameView.invalidate() //redraw screen

    }

    fun setSize(h: Int, w: Int) {
        this.h = h
        this.w = w
    }

    fun movePacmanRight(pixels: Int) {
        //still within our boundaries?
        if (pacx + pixels + pacBitmap.width < w) {
            pacx = pacx + pixels
            doCollisionCheck()
            gameView.invalidate()
        }
    }

    fun movePacmanLeft(pixels: Int) {
        //still within our boundaries?
        if (pacx - pixels + pacBitmap.width > 0 + pacBitmap.width) {
            pacx = pacx - pixels
            doCollisionCheck()
            gameView.invalidate()
        }
    }

    fun movePacmanUp(pixels: Int) {
        //still within our boundaries?
        if (pacy - pixels + pacBitmap.width > 0 + pacBitmap.height) {
            pacy = pacy - pixels
            doCollisionCheck()
            gameView.invalidate()
        }
    }

    fun movePacmanDown(pixels: Int) {
        //still within our boundaries?
        if (pacy + pixels + pacBitmap.width < h) {
            pacy = pacy + pixels
            doCollisionCheck()
            gameView.invalidate()
        }
    }

    //TODO check if the pacman touches a gold coin
    //and if yes, then update the neccesseary data
    //for the gold coins and the points
    //so you need to go through the arraylist of goldcoins and
    //check each of them for a collision with the pacman
    fun doCollisionCheck() {
        var distance: Double

        for (i in 0..coins.size - 1) {


            distance = sqrt(
                pow(
                    (pacx - coins[i].coinx).toDouble(),
                    2.0
                ) + Math.pow((pacy - coins[i].coiny).toDouble(), 2.0)
            )
            if (coins[i].taken == false && distance < 40.0) {
                coins[i].taken = true
                count++
                points++
                pointsView.text = "${context.resources.getString(R.string.points)} $points"


            }

        }
        Log.d("GAMEVIEW", "$count ${coins.size}")
        if (count == coins.size) {

            toastA.show()
        }
    }


}