package org.pondar.pacmankotlin

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.TextView
import android.widget.Toast
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

    var endGame = false

    var counterTime = 60
    var level = 0
    val LEFT = 1
    val RIGHT = 2
    val UP = 3
    val DOWN = 4

    var running = true
    var direction = RIGHT


    val toastLost = Toast.makeText(
        context,
        "You lost the game!",
        Toast.LENGTH_LONG
    )

    //bitmap of the pacman
    var pacBitmap: Bitmap
    var pacx: Int = 0
    var pacy: Int = 0

    var count: Int = 0

    var coinBitmap: Bitmap
    var coinsInitialized = false
    var coins = ArrayList<GoldCoin>()

    var enemyBitmap: Bitmap
    var enemies = ArrayList<Enemy>()
    var enemiesInitialized = false

    //a reference to the gameview
    private lateinit var gameView: GameView
    private var h: Int = 0
    private var w: Int = 0 //height and width of screen


    //The init code is called when we create a new Game class.
    //it's a good place to initialize our images.
    init {
        pacBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.pacman)
        pacBitmap = Bitmap.createScaledBitmap(pacBitmap, 120, 120, true)
        coinBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.coin)
        coinBitmap = Bitmap.createScaledBitmap(coinBitmap, 100, 100, true)
        enemyBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.enemy)
        enemyBitmap = Bitmap.createScaledBitmap(enemyBitmap, 120, 120, true)


    }

    fun setGameView(view: GameView) {
        this.gameView = view
    }

    //TODO initialize goldcoins also here
    fun initializeGoldCoins() {

        var maxX = gameView.w - coinBitmap.width * 2
        var maxY = gameView.h - coinBitmap.height * 2
        coins.add(
            GoldCoin(
                (0 + coinBitmap.width..maxX).random(),
                (0 + coinBitmap.height..maxY).random()
            )
        )
        coinsInitialized = true
    }

    fun initializeEnemy() {

        var eX = (0..gameView.w - enemyBitmap.width).random()
        var eY = (0..gameView.h - enemyBitmap.height).random()
        var pX = pacx
        var pY = pacy
        var distance = distanceBetweenPac(eX, eY, pX, pY)

        while (distance < 350) {
            eX = (0..gameView.w - enemyBitmap.width).random()
            eY = (0..gameView.h - enemyBitmap.height).random()
            distance = distanceBetweenPac(eX, eY, pX, pY)
        }
        enemies.add(Enemy(eX, eY))

        enemiesInitialized = true
    }


    fun distanceBetweenPac(eX: Int, eY: Int, pX: Int, pY: Int): Double {
        var distance = sqrt(
            pow(
                (eX - pX).toDouble(),
                2.0
            ) + Math.pow((eY - pY).toDouble(), 2.0)
        )
        return distance
    }

    fun newGame() {
        direction=RIGHT
        pacx = 50
        pacy = 400
        if (!endGame) {

            level++
            val toastWin = Toast.makeText(
                context,
                "Level ${level}!",
                Toast.LENGTH_SHORT
            )
            toastWin.show()

            points = points
            for (i in 0..coins.size - 1) {
                var maxX = gameView.w - coinBitmap.width * 2
                var maxY = gameView.h - coinBitmap.height * 2
                coins[i].taken = false
                coins[i].coinx = (0 + coinBitmap.width..maxX).random()
                coins[i].coiny = (0 + coinBitmap.height..maxY).random()

            }
            for (i in 0..enemies.size - 1) {

                var eX = (0..gameView.w - enemyBitmap.width).random()
                var eY = (0..gameView.h - enemyBitmap.height).random()
                var pX = pacx
                var pY = pacy
                var distance = distanceBetweenPac(eX, eY, pX, pY)


                while (distance < 350) {
                    eX = (0..gameView.w - enemyBitmap.width).random()
                    eY = (0..gameView.h - enemyBitmap.height).random()
                    distance = distanceBetweenPac(eX, eY, pX, pY)
                }

                enemies[i].enemyx = eX
                enemies[i].enemyy = eY

            }

        } else {
            points = 0
            level = 1
        }


        count = 0

        coinsInitialized = false
        enemiesInitialized = false
        counterTime = 60
        if (gameView.w != 0 && gameView.h != 0) {
            initializeGoldCoins()
            initializeEnemy()
        }

        running = true
        endGame = false
        pointsView.text = "${context.resources.getString(R.string.points)} $points"
        gameView.invalidate() //redraw screen
    }

    fun setSize(h: Int, w: Int) {
        this.h = h
        this.w = w
    }


    fun moveEnemyRight(pixels: Int) {
        //still within our boundaries?
        for (i in 0..enemies.size - 1) {
            if (enemies[i].enemyx + pixels + 140 < w) {
                enemies[i].enemyx = enemies[i].enemyx + pixels
                doCollisionCheckForEnemy()
                gameView.invalidate()
            }
        }
    }


    fun moveEnemyLeft(pixels: Int) {
        //still within our boundaries?
        for (i in 0..enemies.size - 1) {
            if (enemies[i].enemyx - pixels + 140 > 0 + enemyBitmap.width) {
                enemies[i].enemyx = enemies[i].enemyx - pixels
                doCollisionCheckForEnemy()
                gameView.invalidate()
            }
        }
    }

    fun moveEnemyUp(pixels: Int) {
        //still within our boundaries?
        for (i in 0..enemies.size - 1) {
            if (enemies[i].enemyy - pixels + 140 > 0 + 140) {
                enemies[i].enemyy = enemies[i].enemyy - pixels
                doCollisionCheckForEnemy()
                gameView.invalidate()
            }
        }
    }

    fun moveEnemyDown(pixels: Int) {
        //still within our boundaries?
        for (i in 0..enemies.size - 1) {
            if (enemies[i].enemyy + pixels + 140 < h) {
                enemies[i].enemyy = enemies[i].enemyy + pixels
                doCollisionCheckForEnemy()
                gameView.invalidate()
            }
        }
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
        if (count == coins.size) {
            for (i in 0..coins.size - 1) {
                coins[i].taken = false
            }
            running = false
            newGame()
        }
    }

    fun doCollisionCheckForEnemy() {
        var distance: Double
        for (i in 0..enemies.size - 1) {
            distance = sqrt(
                pow(
                    (pacx - enemies[i].enemyx).toDouble(),
                    2.0
                ) + Math.pow((pacy - enemies[i].enemyy).toDouble(), 2.0)
            )
            if (distance < 70.0) {
                toastLost.show()
                endGame = true
                running = false
            }

        }


    }


}