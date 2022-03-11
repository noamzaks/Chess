package com.noamzaks.chess

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.noamzaks.chess.ui.theme.ChessTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChessTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Board(pieces = defaultBoard)
                }
            }
        }
    }
}

abstract class Piece(val isWhite: Boolean) {
    abstract fun getResource(): Int
}

class Pawn(isWhite: Boolean) : Piece(isWhite) {
    override fun getResource(): Int {
        return if (isWhite) R.drawable.alpha_white_pawn else R.drawable.alpha_black_pawn
    }
}

class Knight(isWhite: Boolean) : Piece(isWhite) {
    override fun getResource(): Int {
        return if (isWhite) R.drawable.alpha_white_knight else R.drawable.alpha_black_knight
    }
}

class Bishop(isWhite: Boolean) : Piece(isWhite) {
    override fun getResource(): Int {
        return if (isWhite) R.drawable.alpha_white_bishop else R.drawable.alpha_black_bishop
    }
}

class Rook(isWhite: Boolean) : Piece(isWhite) {
    override fun getResource(): Int {
        return if (isWhite) R.drawable.alpha_white_rook else R.drawable.alpha_black_rook
    }
}

class Queen(isWhite: Boolean) : Piece(isWhite) {
    override fun getResource(): Int {
        return if (isWhite) R.drawable.alpha_white_queen else R.drawable.alpha_black_queen
    }
}

class King(isWhite: Boolean) : Piece(isWhite) {
    override fun getResource(): Int {
        return if (isWhite) R.drawable.alpha_white_king else R.drawable.alpha_black_king
    }
}

val defaultBoard = arrayOf(
    arrayOf(Rook(false), Knight(false), Bishop(false), King(false), Queen(false), Bishop(false), Knight(false), Rook(false)),
    arrayOf(Pawn(false), Pawn(false), Pawn(false), Pawn(false), Pawn(false), Pawn(false), Pawn(false),Pawn(false)),
    arrayOf<Piece?>(null, null, null, null, null, null, null, null),
    arrayOf<Piece?>(null, null, null, null, null, null, null, null),
    arrayOf<Piece?>(null, null, null, null, null, null, null, null),
    arrayOf<Piece?>(null, null, null, null, null, null, null, null),
    arrayOf<Piece?>(null, null, null, null, null, null, null, null),
    arrayOf(Pawn(true), Pawn(true), Pawn(true), Pawn(true), Pawn(true), Pawn(true), Pawn(true),Pawn(true)),
    arrayOf(Rook(true), Knight(true), Bishop(true), King(true), Queen(true), Bishop(true), Knight(true), Rook(true)),
)

@Composable
fun Board(pieces: Array<Array<out Piece?>>) {
    val size = 8
    val isBlack = false

    Column {
        for ((i, row) in pieces.withIndex()) {
            Row {
                for ((j, piece) in row.withIndex()) {
                    val modifier = Modifier
                        .weight(1f)
                        .background(
                            if ((i + j) % 2 == 0) Color(240, 217, 181) else Color(181, 136, 99)
                        )
                    if (piece != null) {
                        Image(
                            painter = painterResource(id = piece.getResource()),
                            contentDescription = "",
                            modifier = modifier
                        )
                    } else {
                        Box(modifier = modifier)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ChessTheme {
        Board(defaultBoard)
    }
}