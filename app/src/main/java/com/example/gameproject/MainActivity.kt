package com.example.gameproject

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gameproject.ui.theme.GameProjectTheme


class MainActivity : ComponentActivity() {
    private lateinit var mediaPlayer: MediaPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var currentScreen by remember { mutableStateOf("Screen1") }
            var questionsList by remember { mutableStateOf(questions.toMutableList()) }
            var currentQuestionIndex by remember { mutableStateOf(0) }
            var score by remember { mutableStateOf(0) }
            var userAnswers by remember { mutableStateOf(listOf<Int>()) }
            var selectedQuestions by remember { mutableStateOf(listOf<Question>()) }

            when (currentScreen) {
                "Screen1" -> WelcomeScreen(
                    onContinueClicked = { currentScreen = "Screen2" }
                )
                "Screen2" -> GameMenuScreen(
                    onStartGameClicked = {
                        userAnswers = listOf()
                        selectedQuestions = questions.shuffled().take(4)
                        currentScreen = "GameA"
                        currentQuestionIndex = 0
                        score = 0
                    },
                    onShowScoreClicked = { currentScreen = "Screen3" },
                    onGoBackClicked = { currentScreen = "Screen1" },
                    onAddQuestionClicked = { currentScreen = "AddQuestion" }
                )
                "GameA" -> {
                    if (currentQuestionIndex < selectedQuestions.size) {
                        GameScreen(
                            question = selectedQuestions[currentQuestionIndex],
                            questionIndex = currentQuestionIndex,
                            onAnswerSelected = { selectedIndex ->
                                // 检查答案是否正确并更新分数
                                if (selectedIndex == selectedQuestions[currentQuestionIndex].correctAnswerIndex) {
                                    score++
                                }
                                userAnswers = userAnswers + selectedIndex
                            },
                            onContinue = {
                                // 处理题目切换逻辑
                                if (currentQuestionIndex < selectedQuestions.size - 1) {
                                    currentQuestionIndex++
                                } else {
                                    currentScreen = "Screen3" // 所有问题都已回答，显示结果
                                }
                            }
                        )
                    }
                }


                "Screen3" -> ResultsScreen(
                    score = score,
                    questions = selectedQuestions,
                    userAnswers = userAnswers,
                    onReturnClicked = { currentScreen = "Screen2" }
                )
                "AddQuestion" -> AddQuestionScreen(onQuestionAdded = { newQuestion ->
                    questionsList.add(newQuestion)
                    currentScreen = "Screen2"
                },
                    onCancel = { currentScreen = "Screen2" }
                )
            }
        }

        setupBackgroundMusic()
    }


    private fun setupBackgroundMusic() {
        mediaPlayer = MediaPlayer.create(this, R.raw.with_an_orchid)
        mediaPlayer.isLooping = true
        mediaPlayer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}

@Composable
fun WelcomeScreen(
    onContinueClicked: () -> Unit,
    modifier: Modifier = Modifier){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.uottawa_logo),
            contentDescription = "Logo",
            modifier = Modifier
                .size(100.dp)
                .padding(bottom = 16.dp)
        )
        Text("Welcome to the Zidu Yin's Game")
        Button(
            modifier = Modifier.padding(vertical = 24.dp),
            onClick = onContinueClicked
        ) {
            Text("OK")
        }
    }
}

@Composable
fun GameMenuScreen(
    onStartGameClicked: () -> Unit,
    onShowScoreClicked: () -> Unit,
    onGoBackClicked: () -> Unit,
    onAddQuestionClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onStartGameClicked,
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Start Game")
        }
        Button(
            onClick = onShowScoreClicked,
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Show Last Score")
        }
        Button(
            onClick = onGoBackClicked,
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Go Back")
        }
        Button(onClick = onAddQuestionClicked) {
            Text("Add Question")
        }
    }
}

data class Question(
    val questionText: String,
    val options: List<String>,
    val correctAnswerIndex: Int
)


val questions = mutableListOf(
    Question("Which country has the most population?", listOf("China", "USA", "India"), 0),
    Question("Which country is the biggest?", listOf("USA", "China", "Russia"), 2),
    Question("Who is the leading actor in the movie Napoleon?", listOf("Joaquin Phoenix", "Tom Cruise", "Tom Hanks"), 0),
    Question("Who is US president?", listOf("Donald Trump", "Joe Biden", "Obama"), 1),
    Question("In what year was The Shawshank Redemption a movie?", listOf("1990", "1998", "1994"), 2),
    Question("What is the capital of France?", listOf("Paris", "Berlin", "London"), 0),
    Question("Which element has the chemical symbol 'O'?", listOf("Gold", "Oxygen", "Silver"), 1),
    Question("Who wrote 'Romeo and Juliet'?", listOf("William Shakespeare", "Charles Dickens", "Leo Tolstoy"), 0),
    Question("What is the largest ocean on Earth?", listOf("Atlantic Ocean", "Indian Ocean", "Pacific Ocean"), 2),
    Question("Who is known as the father of computers?", listOf("Albert Einstein", "Isaac Newton", "Charles Babbage"), 2),
    Question("What year did the first man land on the moon?", listOf("1969", "1972", "1965"), 0),
    Question("Which planet is known as the Red Planet?", listOf("Mars", "Jupiter", "Saturn"), 0),
    Question("What is the hardest natural substance on Earth?", listOf("Diamond", "Gold", "Iron"), 0),
    Question("What is the largest animal in the world?", listOf("African Elephant", "Blue Whale", "Giraffe"), 1),
    Question("Who painted the Mona Lisa?", listOf("Leonardo da Vinci", "Vincent Van Gogh", "Pablo Picasso"), 0),
    Question("What is the smallest country in the world?", listOf("Monaco", "Vatican City", "Nauru"), 1),
    Question("What language has the most words?", listOf("Chinese", "English", "Spanish"), 1),
    Question("Who invented the telephone?", listOf("Alexander Graham Bell", "Thomas Edison", "Nikola Tesla"), 0),
    Question("In which city were the 2008 Summer Olympics held?", listOf("Beijing", "London", "Sydney"), 0),
    Question("What is the boiling point of water?", listOf("100°C", "90°C", "110°C"), 0)
)

@Composable
fun GameScreen(
    question: Question,
    questionIndex: Int,
    onAnswerSelected: (Int) -> Unit,  // 更改回调函数签名
    onContinue: () -> Unit
) {
    val colors = listOf(
        Color(0xFFE57373), // 浅红色
        Color(0xFF81C784), // 浅绿色
        Color(0xFF64B5F6), // 浅蓝色
        Color(0xFFFFF176)  // 浅黄色
    )

    val backgroundColor = colors[questionIndex % colors.size]
    var selectedAnswerIndex by remember { mutableStateOf<Int?>(null) }  // 记录用户的答案选择
    // 当问题索引改变时，重置选择的答案索引
    LaunchedEffect(questionIndex) {
        selectedAnswerIndex = null
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("${questionIndex + 1}: ${question.questionText}")
        question.options.forEachIndexed { index, option ->
            val optionLabel = when (index) {
                0 -> "a"
                1 -> "b"
                2 -> "c"
                else -> ""
            }
            Button(
                onClick = {
                    selectedAnswerIndex = index  // 更新用户选择的答案索引
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("$optionLabel. $option")
            }
        }
        Button(
            onClick = {
                onAnswerSelected(selectedAnswerIndex ?: -1)  // 用户未作答则传递 -1
                onContinue()
            },
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text("OK")
        }
    }
}



@Composable
fun ResultsScreen(
    score: Int,
    questions: List<Question>,
    userAnswers: List<Int>,
    onReturnClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Your Score: $score")
        questions.forEachIndexed { index, question ->
            val userAnswerIndex = userAnswers.getOrNull(index) ?: -1
            Log.d("ResultsScreen", "Question: ${question.questionText}, User Answer Index: $userAnswerIndex, Correct Answer Index: ${question.correctAnswerIndex}")
            val isCorrect = question.correctAnswerIndex == userAnswerIndex
            val answerFeedback = if (isCorrect) {
                "Correct!"
            } else {
                "Wrong! Correct Answer: ${question.options[question.correctAnswerIndex]}"
            }
            val textStyle = if (isCorrect) TextStyle(fontWeight = FontWeight.Normal) else TextStyle(fontWeight = FontWeight.Bold)

            Text(
                text = "${index + 1}. ${question.questionText} - Your Answer: ${question.options.getOrElse(userAnswerIndex) { "Not answered" }} - $answerFeedback",
                style = textStyle
            )
        }
        Button(
            onClick = onReturnClicked,
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text("Return")
        }
    }
}

@Composable
fun ShowScoreScreen(
    lastScoreGameA: Int,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Last Score for Game A: $lastScoreGameA")
        Button(
            onClick = onBackClicked,
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text("Back")
        }
    }
}

@Composable
fun AddQuestionScreen(onQuestionAdded: (Question) -> Unit,onCancel: () -> Unit) {
    var questionText by remember { mutableStateOf("") }
    var optionA by remember { mutableStateOf("") }
    var optionB by remember { mutableStateOf("") }
    var optionC by remember { mutableStateOf("") }
    var correctAnswerIndex by remember { mutableStateOf(0) }

    val isInputValid = questionText.isNotBlank() &&
            optionA.isNotBlank() &&
            optionB.isNotBlank() &&
            optionC.isNotBlank()

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = questionText,
            onValueChange = { questionText = it },
            label = { Text("Enter question") }
        )
        TextField(
            value = optionA,
            onValueChange = { optionA = it },
            label = { Text("Option A") }
        )
        TextField(
            value = optionB,
            onValueChange = { optionB = it },
            label = { Text("Option B") }
        )
        TextField(
            value = optionC,
            onValueChange = { optionC = it },
            label = { Text("Option C") }
        )

        Row {
            RadioButton(
                selected = correctAnswerIndex == 0,
                onClick = { correctAnswerIndex = 0 }
            )
            Text("A")
            RadioButton(
                selected = correctAnswerIndex == 1,
                onClick = { correctAnswerIndex = 1 }
            )
            Text("B")
            RadioButton(
                selected = correctAnswerIndex == 2,
                onClick = { correctAnswerIndex = 2 }
            )
            Text("C")
        }

        Button(onClick = {
            if (isInputValid) {
                val newQuestion = Question(questionText, listOf(optionA, optionB, optionC), correctAnswerIndex)
                onQuestionAdded(newQuestion)
                questions.add(newQuestion)
            }
        },
            enabled = isInputValid) {
            Text("Add Question")
        }
        Button(onClick = onCancel) {
            Text("Cancel")
        }
    }
}


@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun WelcomeScreenPreview() {
    GameProjectTheme {
        WelcomeScreen(onContinueClicked = {})
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun GameMenuScreenPreview() {
    GameProjectTheme {
        GameMenuScreen(onStartGameClicked = {},onShowScoreClicked={},onGoBackClicked={}, onAddQuestionClicked = {})
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun ShowScoreScreenPreview() {
    GameProjectTheme {
        ShowScoreScreen(lastScoreGameA = 0,onBackClicked={})
    }
}


@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun PreviewGameScreen() {
    val sampleQuestion = Question(
        "What is the capital of France?",
        listOf("Paris", "Berlin", "London"),
        0
    )

    GameProjectTheme {
        GameScreen(
            question = sampleQuestion,
            questionIndex = 0,
            onAnswerSelected = {_ ->},
            onContinue = {}
        )
    }
}


@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun PreviewResultsScreen() {
    val sampleQuestions = listOf(
        Question("What is the capital of France?", listOf("Paris", "Berlin", "London"), 0),

        )
    val sampleUserAnswers = listOf(0)
    GameProjectTheme {
        ResultsScreen(
            score = 3,
            questions = sampleQuestions,
            userAnswers = sampleUserAnswers,
            onReturnClicked = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAddQuestionScreen() {
    GameProjectTheme {
        AddQuestionScreen(
            onQuestionAdded = { /* TODO */ },
            onCancel = { /* TODO */ }
        )
    }
}
