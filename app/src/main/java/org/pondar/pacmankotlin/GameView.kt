package org.pondar.pacmankotlin

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View


//note we now create our own view class that extends the built-in View class
class GameView : View {

    private lateinit var game: Game
     public var h: Int = 0
     public var w: Int = 0 //used for storing our height and width of the view

    fun setGame(game: Game) {
        this.game = game
    }


    /* The next 3 constructors are needed for the Android view system,
	when we have a custom view.
	 */
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)


    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    //In the onDraw we put all our code that should be
    //drawn whenever we update the screen.
    override fun onDraw(canvas: Canvas) {
        //Here we get the height and weight
        h = height
        w = width
        //update the size for the canvas to the game.
        game.setSize(h, w)
        Log.d("GAMEVIEW", "h = $h, w = $w")

        //are the coins initiazlied?
        //if not initizlise them
        if (!(game.coinsInitialized))
            game.initializeGoldCoins()

        if (!(game.enemiesInitialized))
            game.initializeEnemy()

        //Making a new paint object
        val paint = Paint()
        canvas.drawColor(Color.WHITE) //clear entire canvas to white color

        for (i in 0..game.enemies.size - 1) {
            if (game.enemies[i].isALive == false) {

            } else {
                canvas.drawBitmap(
                    game.enemyBitmap,
                    game.enemies[i].enemyx.toFloat(),
                    game.enemies[i].enemyy.toFloat(),
                    paint
                )

            }
        }


        for (i in 0..game.coins.size - 1) {
            if (game.coins[i].taken == true) {

            } else {
                canvas.drawBitmap(
                    game.coinBitmap,
                    game.coins[i].coinx.toFloat(),
                    game.coins[i].coiny.toFloat(),
                    paint
                )
            }
        }
        //draw the pacman
        canvas.drawBitmap(
            game.pacBitmap, game.pacx.toFloat(),
            game.pacy.toFloat(), paint
        )



        game.doCollisionCheck()

        super.onDraw(canvas)

    }

}
