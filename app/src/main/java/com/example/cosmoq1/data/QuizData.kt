package com.example.cosmoq1.data

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctIndex: Int
)

val quizQuestions = listOf(
    QuizQuestion(
        question = "Which planet is known as the Red Planet?",
        options = listOf("Venus", "Mars", "Jupiter", "Saturn"),
        correctIndex = 1
    ),
    QuizQuestion(
        question = "Which is the largest planet in our solar system?",
        options = listOf("Saturn", "Uranus", "Neptune", "Jupiter"),
        correctIndex = 3
    ),
    QuizQuestion(
        question = "How many moons does Earth have?",
        options = listOf("0", "1", "2", "3"),
        correctIndex = 1
    ),
    QuizQuestion(
        question = "Which planet has the most moons?",
        options = listOf("Jupiter", "Saturn", "Uranus", "Neptune"),
        correctIndex = 1
    ),
    QuizQuestion(
        question = "Which planet rotates on its side?",
        options = listOf("Neptune", "Venus", "Uranus", "Mercury"),
        correctIndex = 2
    ),
    QuizQuestion(
        question = "What is the hottest planet in our solar system?",
        options = listOf("Mercury", "Mars", "Venus", "Jupiter"),
        correctIndex = 2
    ),
    QuizQuestion(
        question = "Which planet has a Great Red Spot?",
        options = listOf("Mars", "Saturn", "Neptune", "Jupiter"),
        correctIndex = 3
    ),
    QuizQuestion(
        question = "Which planet is closest to the Sun?",
        options = listOf("Venus", "Earth", "Mercury", "Mars"),
        correctIndex = 2
    ),
    QuizQuestion(
        question = "Which planet is known for its spectacular ring system?",
        options = listOf("Jupiter", "Saturn", "Uranus", "Neptune"),
        correctIndex = 1
    ),
    QuizQuestion(
        question = "Which planet has the strongest winds in the solar system?",
        options = listOf("Jupiter", "Saturn", "Uranus", "Neptune"),
        correctIndex = 3
    ),
    QuizQuestion(
        question = "What is the smallest planet in our solar system?",
        options = listOf("Mars", "Venus", "Mercury", "Pluto"),
        correctIndex = 2
    ),
    QuizQuestion(
        question = "Which planet is least dense and could float on water?",
        options = listOf("Jupiter", "Uranus", "Neptune", "Saturn"),
        correctIndex = 3
    ),
    QuizQuestion(
        question = "How long does light take to travel from the Sun to Earth?",
        options = listOf("About 8 minutes", "About 1 hour", "About 1 second", "About 1 day"),
        correctIndex = 0
    ),
    QuizQuestion(
        question = "Which planet was the first discovered using a telescope?",
        options = listOf("Neptune", "Saturn", "Uranus", "Jupiter"),
        correctIndex = 2
    ),
    QuizQuestion(
        question = "What type of planet is Jupiter?",
        options = listOf("Terrestrial", "Ice Giant", "Gas Giant", "Dwarf Planet"),
        correctIndex = 2
    )
)
