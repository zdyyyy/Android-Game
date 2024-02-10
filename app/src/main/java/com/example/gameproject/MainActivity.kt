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
import androidx.compose.material3.AlertDialog
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
            var currentScreen by remember { mutableStateOf("WelcomeScreen") }
            var questionsList by remember { mutableStateOf(questions.toMutableList()) }
            var currentQuestionIndex by remember { mutableStateOf(0) }
            var score by remember { mutableStateOf(0) }
            var userAnswers by remember { mutableStateOf(listOf<Int>()) }
            var selectedQuestions by remember { mutableStateOf(listOf<Question>()) }

            when (currentScreen) {
                // Screen1
                "WelcomeScreen" -> WelcomeScreen(
                    onContinueClicked = { currentScreen = "GameMenuScreen" }
                )
                // Screen2
                "GameMenuScreen" -> GameMenuScreen(
                    onStartGameClicked = {
                        userAnswers = listOf()
                        //randomly select 4 four questions from question list
                        selectedQuestions = questions.shuffled().take(4)
                        currentScreen = "GameA"
                        currentQuestionIndex = 0
                        score = 0
                    },
                    onShowScoreClicked = { currentScreen = "ShowScoreScreen" },
                    onGoBackClicked = { currentScreen = "WelcomeScreen" },
                    onAddQuestionClicked = { currentScreen = "AddQuestion" }
                )

                "GameA" -> {
                    if (currentQuestionIndex < selectedQuestions.size) {
                        GameScreen(
                            question = selectedQuestions[currentQuestionIndex],
                            questionIndex = currentQuestionIndex,
                            onAnswerSelected = { selectedIndex ->
                                // Check if the answer is correct and update the score
                                if (selectedIndex == selectedQuestions[currentQuestionIndex].correctAnswerIndex) {
                                    score++
                                }
                                userAnswers = userAnswers + selectedIndex
                            },
                            onContinue = {
                                // Switch Questions
                                if (currentQuestionIndex < selectedQuestions.size - 1) {
                                    currentQuestionIndex++
                                } else {
                                    currentScreen = "ResultsScreen" // All questions have been answered, display the results
                                }
                            }
                        )
                    }
                }

                // Screen3
                "ShowScoreScreen" -> ShowScoreScreen(
                    lastScoreGameA = score,
                    onBackClicked = { currentScreen = "GameMenuScreen" }
                )

                "ResultsScreen" -> ResultsScreen(
                    score = score,
                    questions = selectedQuestions,
                    userAnswers = userAnswers,
                    onReturnClicked = { currentScreen = "GameMenuScreen" }
                )

                "AddQuestion" -> AddQuestionScreen(
                    questionList = questionsList,
                    onQuestionAdded = {
                        newQuestion ->
                        // If the new question is not in the list, add it directly
                        if (questionsList.none { it.questionText == newQuestion.questionText }) {
                            questionsList.add(newQuestion)
                        }
                        currentScreen = "GameMenuScreen"
                    },
                    onConfirmReplace = {
                        newQuestion ->
                        // If the new question is in the list, find it and replace
                        val index =
                            questionsList.indexOfFirst { it.questionText == newQuestion.questionText }
                        if (index != -1) {
                            questionsList[index] = newQuestion
                        }
                        currentScreen = "GameMenuScreen"
                    },
                    onCancel = { currentScreen = "GameMenuScreen" }
                )
            }
        }
        //Background music
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
        //uottawa logo
        Image(
            painter = painterResource(id = R.drawable.uottawa_logo),
            contentDescription = "Logo",
            modifier = Modifier
                .size(100.dp)
                .padding(bottom = 16.dp)
        )
        //welcome text
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
        //Button 1
        Button(
            onClick = onStartGameClicked,
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Start Game")
        }
        //Button 2
        Button(
            onClick = onShowScoreClicked,
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Show Last Score")
        }
        //Button 3
        Button(
            onClick = onGoBackClicked,
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Go Back")
        }
        //Button 4
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
    onAnswerSelected: (Int) -> Unit,
    onContinue: () -> Unit
) {
    val colors = listOf(
        Color(0xFFE57373), // light red
        Color(0xFF81C784), // light green
        Color(0xFF64B5F6), // light blue
        Color(0xFFFFF176)  // light yellow
    )

    val backgroundColor = colors[questionIndex % colors.size]
    var selectedAnswerIndex by remember { mutableStateOf<Int?>(null) }  // Record the user's answer choices
    // When the question index changes, reset the selected answer index
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
        //Add index(a,b,c)
        question.options.forEachIndexed { index, option ->
            val optionLabel = when (index) {
                0 -> "a"
                1 -> "b"
                2 -> "c"
                else -> ""
            }
            Button(
                onClick = {
                    selectedAnswerIndex = index  // Update the answer index selected by the user
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("$optionLabel. $option")
            }
        }
        Button(
            onClick = {
                onAnswerSelected(selectedAnswerIndex ?: -1)  // Pass -1 if the user does not answer
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
            //Get user's answer, return -1 if not answer
            val userAnswerIndex = userAnswers.getOrNull(index) ?: -1
            Log.d("ResultsScreen", "Question: ${question.questionText}, User Answer Index: $userAnswerIndex, Correct Answer Index: ${question.correctAnswerIndex}")
            //When user's answer index is equal to correct answer index
            val isCorrect = question.correctAnswerIndex == userAnswerIndex
            //Provide feedback
            val answerFeedback = if (isCorrect) {
                "Correct!"
            } else {
                "Wrong! Correct Answer: ${question.options[question.correctAnswerIndex]}"
            }
            //Highlight for wrong answer
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
        //The score of the last game the user played
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
fun AddQuestionScreen(
    questionList: List<Question>,
    onQuestionAdded: (Question) -> Unit,
    onConfirmReplace: (Question) -> Unit,
    onCancel: () -> Unit) {
    var questionText by remember { mutableStateOf("") }
    var optionA by remember { mutableStateOf("") }
    var optionB by remember { mutableStateOf("") }
    var optionC by remember { mutableStateOf("") }
    var correctAnswerIndex by remember { mutableStateOf(0) }

    // Marks whether to show the overwrite confirmation dialog
    var showConfirmReplaceDialog by remember { mutableStateOf(false) }
    var questionToReplaceIndex by remember { mutableStateOf(-1) }

    val isInputValid = questionText.isNotBlank() &&
            optionA.isNotBlank() &&
            optionB.isNotBlank() &&
            optionC.isNotBlank()

    val existingQuestionIndex = questionList.indexOfFirst { it.questionText == questionText }
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
                if (existingQuestionIndex >= 0){
                    //The question already exists and requires confirmation of coverage.
                    showConfirmReplaceDialog = true
                    questionToReplaceIndex = existingQuestionIndex
                } else {
                    val newQuestion = Question(
                        questionText,
                        listOf(optionA, optionB, optionC),
                        correctAnswerIndex
                    )
                    onQuestionAdded(newQuestion)
                    questions.add(newQuestion)
                }
            }
        },
            enabled = isInputValid) {
            Text("Add Question")
        }
        Button(onClick = onCancel) {
            Text("Cancel")
        }
    }
    if (showConfirmReplaceDialog){
        AlertDialog(
            onDismissRequest = {
                showConfirmReplaceDialog = false
            },
            title = { Text("Confirm Replace") },
            text = { Text("This question already exists. Do you want to replace it?")},
            confirmButton = {
                Button(
                    onClick = {
                        // User confirms overwriting and replaces the problem
                        val newQuestion = Question(questionText, listOf(optionA,optionB,optionC), correctAnswerIndex)
                        // Delete questions with the same topic first
                        questions.removeAll { it.questionText == questionText }
                        // Then add a new question
                        questions.add(newQuestion)
                        onConfirmReplace(newQuestion)
                        showConfirmReplaceDialog = false
                    }
                ){
                    Text("Replace")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        // User cancel overwriting
                        showConfirmReplaceDialog = false
                }) {
                    Text("Cancel")
                }
            }
        )
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

        val emptyQuestionList = listOf<Question>()

        AddQuestionScreen(
            questionList = emptyQuestionList,
            onQuestionAdded = { /* TODO */ },
            onConfirmReplace = { /* TODO */ },
            onCancel = { /* TODO */ }
        )
    }
}

