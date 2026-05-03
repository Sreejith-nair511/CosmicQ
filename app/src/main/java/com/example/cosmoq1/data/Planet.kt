package com.example.cosmoq1.data

data class Planet(
    val name: String,
    val shortDescription: String,
    val fullDescription: String,
    val funFacts: List<String>,
    val colorHex: Long,
    val distanceFromSun: String,
    val diameter: String,
    val moons: Int
)

val samplePlanets = listOf(
    Planet(
        name = "Mercury",
        shortDescription = "The smallest and fastest planet, closest to the Sun.",
        fullDescription = "Mercury is the smallest planet in our solar system and the closest to the Sun. Despite being closest to the Sun, it is not the hottest planet — that title belongs to Venus. Mercury has a very thin atmosphere, which means temperatures swing wildly between day and night.",
        funFacts = listOf(
            "A year on Mercury is only 88 Earth days.",
            "Mercury has no moons and no rings.",
            "Surface temperatures range from -180°C to 430°C.",
            "Mercury is only slightly larger than Earth's Moon.",
            "It has a large iron core making up about 85% of its radius."
        ),
        colorHex = 0xFFB5B5B5,
        distanceFromSun = "57.9 million km",
        diameter = "4,879 km",
        moons = 0
    ),
    Planet(
        name = "Venus",
        shortDescription = "The hottest planet, shrouded in thick toxic clouds.",
        fullDescription = "Venus is the second planet from the Sun and is Earth's closest planetary neighbor. It's the hottest planet in our solar system, even though Mercury is closer to the Sun. Venus has a thick, toxic atmosphere filled with carbon dioxide and clouds of sulfuric acid.",
        funFacts = listOf(
            "Venus rotates backwards compared to most planets.",
            "A day on Venus is longer than a year on Venus.",
            "Surface temperature reaches 465°C — hot enough to melt lead.",
            "Venus is the brightest natural object in the night sky after the Moon.",
            "It has over 1,600 major volcanoes on its surface."
        ),
        colorHex = 0xFFE8C56A,
        distanceFromSun = "108.2 million km",
        diameter = "12,104 km",
        moons = 0
    ),
    Planet(
        name = "Earth",
        shortDescription = "Our home — the only known planet with life.",
        fullDescription = "Earth is the third planet from the Sun and the only astronomical object known to harbor life. About 71% of Earth's surface is covered with water. Earth's atmosphere protects life from harmful solar radiation and keeps the planet warm enough to sustain liquid water.",
        funFacts = listOf(
            "Earth is the densest planet in the solar system.",
            "Earth's magnetic field protects us from solar wind.",
            "The Moon stabilizes Earth's axial tilt, enabling stable seasons.",
            "Earth is the only planet not named after a god.",
            "One day on Earth is exactly 23 hours, 56 minutes, and 4 seconds."
        ),
        colorHex = 0xFF4A90D9,
        distanceFromSun = "149.6 million km",
        diameter = "12,742 km",
        moons = 1
    ),
    Planet(
        name = "Mars",
        shortDescription = "The Red Planet — a cold desert world with the tallest volcano.",
        fullDescription = "Mars is the fourth planet from the Sun and the second-smallest planet in the solar system. Often called the 'Red Planet' due to its reddish appearance caused by iron oxide on its surface. Mars has the tallest volcano and the longest canyon in the solar system.",
        funFacts = listOf(
            "Olympus Mons on Mars is the tallest volcano in the solar system at 22 km high.",
            "Mars has two small moons: Phobos and Deimos.",
            "A Martian day (sol) is 24 hours and 37 minutes.",
            "Mars has the largest dust storms in the solar system.",
            "Evidence suggests Mars once had liquid water on its surface."
        ),
        colorHex = 0xFFD45F2E,
        distanceFromSun = "227.9 million km",
        diameter = "6,779 km",
        moons = 2
    ),
    Planet(
        name = "Jupiter",
        shortDescription = "The largest planet — a giant storm world with 95 moons.",
        fullDescription = "Jupiter is the fifth planet from the Sun and the largest in the solar system. It is a gas giant with a mass more than twice that of all the other planets combined. Jupiter's iconic Great Red Spot is a storm that has been raging for hundreds of years.",
        funFacts = listOf(
            "Jupiter's Great Red Spot is a storm larger than Earth.",
            "Jupiter has 95 known moons, including the four large Galilean moons.",
            "A day on Jupiter is only about 10 hours long.",
            "Jupiter acts as a 'cosmic vacuum cleaner', protecting inner planets from asteroids.",
            "Jupiter emits more heat than it receives from the Sun."
        ),
        colorHex = 0xFFD4A96A,
        distanceFromSun = "778.5 million km",
        diameter = "139,820 km",
        moons = 95
    ),
    Planet(
        name = "Saturn",
        shortDescription = "The ringed jewel of the solar system.",
        fullDescription = "Saturn is the sixth planet from the Sun and the second-largest in the solar system. It is a gas giant with an average radius about nine and a half times that of Earth. Saturn is best known for its stunning ring system, which is made up of ice and rock particles.",
        funFacts = listOf(
            "Saturn's rings are made of ice and rock, spanning up to 282,000 km.",
            "Saturn is the least dense planet — it could float on water.",
            "Saturn has 146 known moons, including Titan with a thick atmosphere.",
            "Winds on Saturn can reach 1,800 km/h.",
            "Saturn takes 29.5 Earth years to orbit the Sun."
        ),
        colorHex = 0xFFF4D03F,
        distanceFromSun = "1.43 billion km",
        diameter = "116,460 km",
        moons = 146
    ),
    Planet(
        name = "Uranus",
        shortDescription = "The ice giant that rotates on its side.",
        fullDescription = "Uranus is the seventh planet from the Sun. It has the third-largest planetary radius and fourth-largest planetary mass in the solar system. Uranus is an ice giant — its interior is mainly composed of icy materials. It rotates on its side with an axial tilt of 98 degrees.",
        funFacts = listOf(
            "Uranus rotates on its side — its axial tilt is 98 degrees.",
            "Uranus has 13 known rings and 28 known moons.",
            "It is the coldest planetary atmosphere in the solar system at -224°C.",
            "Uranus was the first planet discovered with a telescope, in 1781.",
            "A year on Uranus equals 84 Earth years."
        ),
        colorHex = 0xFF7DE8E8,
        distanceFromSun = "2.87 billion km",
        diameter = "50,724 km",
        moons = 28
    ),
    Planet(
        name = "Neptune",
        shortDescription = "The windiest planet — a dark, cold, supersonic world.",
        fullDescription = "Neptune is the eighth and farthest known planet from the Sun. It is the fourth-largest planet by diameter and the third-largest by mass. Neptune is 17 times the mass of Earth and is slightly more massive than its near-twin Uranus. It has the strongest winds in the solar system.",
        funFacts = listOf(
            "Neptune has the strongest winds in the solar system — up to 2,100 km/h.",
            "Neptune has 16 known moons; the largest is Triton.",
            "Neptune was predicted mathematically before it was observed.",
            "A year on Neptune is 165 Earth years.",
            "Neptune's Great Dark Spot was a storm the size of Earth."
        ),
        colorHex = 0xFF4169E1,
        distanceFromSun = "4.5 billion km",
        diameter = "49,244 km",
        moons = 16
    )
)
