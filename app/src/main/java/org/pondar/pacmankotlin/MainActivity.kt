package org.pondar.pacmankotlin

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import org.pondar.pacmankotlin.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    //reference to the game class.
    private lateinit var game: Game
    private lateinit var binding: ActivityMainBinding

    private var pacmanTimer: Timer = Timer()
    private var time: Timer = Timer()


    /// var counterForPacman: Int = 0
    //var countTime: Int = 60
    //constants for directions - define the rest yourself


    //you should put the "running" and "direction" variable in the game class


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        //makes sure it always runs in portrait mode - will cost a warning
        //but this is want we want!
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        Log.d("onCreate", "Oncreate called")


        // running = true //should the game be running?
        //We will call the timer 5 times each second
        pacmanTimer.schedule(object : TimerTask() {
            override fun run() {
                timerMethod()
            }

        }, 3000, 200) //0 indicates we start now, 200
        //is the number of miliseconds between each call
        time.schedule(object : TimerTask() {
            override fun run() {
                time()
            }

        }, 3000, 1000)

        binding.startButton.setOnClickListener(this)
        binding.stopButton.setOnClickListener(this)



        game = Game(this, binding.pointsView)

        //intialize the game view clas and game class

        game.setGameView(binding.gameView)
        binding.gameView.setGame(game)
        game.newGame()


        view.setOnTouchListener(object : OnSwipeTouchListener(baseContext) {

            override fun onSwipeTop() {
                super.onSwipeTop()
                game.direction = game.UP
            }

            override fun onSwipeBottom() {
                super.onSwipeBottom()
                game.direction = game.DOWN
            }

            override fun onSwipeLeft() {
                super.onSwipeLeft()
                game.direction = game.LEFT
            }

            override fun onSwipeRight() {
                super.onSwipeRight()
                game.direction = game.RIGHT
            }
        })

    }

    override fun onStop() {
        super.onStop()
        pacmanTimer.cancel()
        time.cancel()
    }

    private fun timerMethod() {
        this.runOnUiThread(timerTick)
    }

    private fun time() {
        this.runOnUiThread(timer)
    }

    private val timer = Runnable {
        if (game.running) {

            game.counterTime--
            if (game.counterTime == 0) {
                Toast.makeText(this, "You lose!", Toast.LENGTH_LONG).show()
                game.running = false
                game.endGame = true
            }

            //update the counter - notice this is NOT seconds in this example
            //you need TWO counters - one for the timer count down that will
            // run every second and one for the pacman which need to run
            //faster than every second
            binding.textView.text = getString(R.string.timerValue, game.counterTime)

        }
        binding.levelView.text = getString(R.string.levelValue, game.level)
    }
    private val timerTick = Runnable {
        //This method runs in the same thread as the UI.
        // so we can draw

        if (game.running) {


            //game.moveEnemyRight(40)
            if (game.direction == game.RIGHT) { // move right
                game.movePacmanRight(30)
                game.moveEnemyLeft(40)
            } else if (game.direction == game.LEFT) {
                game.movePacmanLeft(30)
                game.moveEnemyRight(40)
            } else if (game.direction == game.UP) {
                game.movePacmanUp(30)
                game.moveEnemyDown(40)
            } else if (game.direction == game.DOWN) {
                game.movePacmanDown(30)
                game.moveEnemyUp(40)
            }
        }

    }

    //if anything is pressed - we do the checks here
    override fun onClick(v: View) {
        if (v.id == R.id.startButton) {
            if (game.endGame == false) {
                game.running = true
            }
        } else if (v.id == R.id.stopButton) {
            game.running = false
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == R.id.action_settings) {
            Toast.makeText(this, "settings clicked", Toast.LENGTH_SHORT).show()
            return true
        } else if (id == R.id.action_newGame) {
            Toast.makeText(this, "New Game clicked", Toast.LENGTH_SHORT).show()
            game.coins.clear()
            game.enemies.clear()
            game.coinsInitialized = false;
            game.direction = game.RIGHT
            game.counterTime = 60
            game.running = true
            binding.textView.text = getString(R.string.timerValue, game.counterTime)
            binding.levelView.text = getString(R.string.levelValue, game.level)
            game.endGame = true
            game.newGame()

            return true
        }
        return super.onOptionsItemSelected(item)
    }
}