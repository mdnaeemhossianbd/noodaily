package com.example.data.seed

import com.example.data.model.Category
import com.example.data.model.Post
import com.example.data.model.Quiz
import com.example.data.model.QuizQuestion
import com.example.data.model.Subcategory

object InitialDataSeed {
    val categories = listOf(
        Category(
            id = "cat_quran",
            name = "Quran & Tajweed Learning",
            nameBn = "কুরআন ও তাজবীদ শিক্ষা",
            description = "Learn Quran verses, word meanings, and Tajweed pronunciation rules.",
            icon = "menu_book",
            order = 1,
            isActive = true
        ),
        Category(
            id = "cat_hadith",
            name = "Hadith & Fiqh Studies",
            nameBn = "হাদিস ও ফিকহ পাঠ",
            description = "Authentic Sahih Hadith with verified references and daily-life rulings.",
            icon = "auto_stories",
            order = 2,
            isActive = true
        ),
        Category(
            id = "cat_kids",
            name = "Kids Islamic Education",
            nameBn = "শিশুদের ইসলাম শিক্ষা",
            description = "Essential Kalimas, Wudu, Salah steps, and inspiring stories of Prophets.",
            icon = "child_care",
            order = 3,
            isActive = true
        ),
        Category(
            id = "cat_quiz",
            name = "Islamic Quiz & Trivia",
            nameBn = "ইসলামিক কুইজ ও জ্ঞান",
            description = "Test your Quranic and Islamic knowledge with interactive multiple-choice questions.",
            icon = "quiz",
            order = 4,
            isActive = true
        ),
        Category(
            id = "cat_dua",
            name = "Daily Dua & Azkar",
            nameBn = "দৈনিক দুআ ও আজকার",
            description = "Authentic morning, evening, meal, and travel duas with Arabic and Bengali.",
            icon = "favorite",
            order = 5,
            isActive = true
        )
    )

    val subcategories = listOf(
        // Quran subcategories
        Subcategory(
            id = "sub_quran_verses",
            categoryId = "cat_quran",
            name = "Quran Verses with Translation",
            nameBn = "অর্থসহ কুরআনের আয়াত",
            order = 1
        ),
        Subcategory(
            id = "sub_tajweed_rules",
            categoryId = "cat_quran",
            name = "Tajweed Rules",
            nameBn = "তাজবীদের নিয়মাবলি",
            order = 2
        ),
        Subcategory(
            id = "sub_arabic_pronunciation",
            categoryId = "cat_quran",
            name = "Arabic Pronunciation Lessons",
            nameBn = "সহীহ উচ্চারণ ও মাখরাজ",
            order = 3
        ),
        // Hadith subcategories
        Subcategory(
            id = "sub_sahih_hadith",
            categoryId = "cat_hadith",
            name = "Sahih Hadith",
            nameBn = "সহীহ হাদিস সম্ভার",
            order = 1
        ),
        Subcategory(
            id = "sub_daily_life_rulings",
            categoryId = "cat_hadith",
            name = "Daily-life Islamic Rulings",
            nameBn = "দৈনন্দিন জীবনের মাসআলা",
            order = 2
        ),
        // Kids subcategories
        Subcategory(
            id = "sub_kids_kalima",
            categoryId = "cat_kids",
            name = "Kalima & Iman",
            nameBn = "কালিমা ও ঈমান শিক্ষা",
            order = 1
        ),
        Subcategory(
            id = "sub_kids_prophets",
            categoryId = "cat_kids",
            name = "Stories of the Prophets",
            nameBn = "নবীদের জীবনী ও শিক্ষা",
            order = 2
        ),
        // Dua subcategories
        Subcategory(
            id = "sub_morning_evening",
            categoryId = "cat_dua",
            name = "Morning & Evening Azkar",
            nameBn = "সকাল ও সন্ধ্যার যিকির",
            order = 1
        ),
        Subcategory(
            id = "sub_sleeping_waking",
            categoryId = "cat_dua",
            name = "Sleep & Daily Routine Duas",
            nameBn = "ঘুম ও নিত্যদিনের দুআ",
            order = 2
        )
    )

    val posts = listOf(
        Post(
            id = "post_ayatul_kursi",
            title = "Ayatul Kursi — The Greatest Verse of the Quran",
            titleBn = "আয়াতুল কুরসী — কুরআনের শ্রেষ্ঠ আয়াত ও এর মাহাত্ম্য",
            subtitle = "Surah Al-Baqarah, Verse 255 with authentic virtues and tafsir notes",
            arabicText = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ مَن ذَا الَّذِي يَشْفَعُ عِندَهُ إِلَّا بِإِذْنِهِ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَيْءٍ مِّنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ ۖ وَلَا يَئُودُهُ حِفْظُهُمَا ۚ وَهُوَ الْعَلِيُّ الْعَظِيمُ",
            bengaliText = "আল্লাহ, তিনি ছাড়া কোনো সত্য উপাস্য নেই; তিনি চিরঞ্জীব, সর্বসত্তার ধারক। তন্দ্রা বা নিদ্রা তাঁকে স্পর্শ করে না। আকাশমণ্ডল ও ভূমণ্ডলে যা কিছু রয়েছে, সবই তাঁর। কে আছে এমন যে তাঁর অনুমতি ছাড়া তাঁর নিকট সুপারিশ করতে পারে? মানুষের সম্মুখে ও পেছনে যা কিছু আছে তা তিনি জানেন। তাঁর জ্ঞানের কোনো কিছু তারা আয়ত্ত করতে পারে না, কেবল তিনি যা ইচ্ছা করেন তা ব্যতীত। তাঁর কুরসী সমস্ত আসমান ও জমিন পরিব্যাপ্ত করে আছে; আর এ দুটোর সংরক্ষণ তাঁকে ক্লান্ত করে না। তিনি পরম মহিয়ান, মহামহিম।",
            englishText = "Allah - there is no deity except Him, the Ever-Living, the Sustainer of all existence. Neither drowsiness overtakes Him nor sleep. To Him belongs whatever is in the heavens and whatever is on the earth. Who is it that can intercede with Him except by His permission? He knows what is before them and what will be after them, and they encompass not a thing of His knowledge except for what He wills. His Kursi extends over the heavens and the earth, and their preservation tires Him not. And He is the Most High, the Most Great.",
            transliteration = "Allahu la ilaha illa Huwa, Al-Hayyul-Qayyum. La ta'khudhuhu sinatuw-wa la nawm. Lahu ma fis-samawati wa ma fil-ard. Man dhal-ladhi yashfa'u 'indahu illa bi-idhnihi? Ya'lamu ma bayna aydihim wa ma khalfahum, wa la yuhituna bi-shay'im-min 'ilmihi illa bima sha'. Wasi'a kursiyyuhus-samawati wal-ard, wa la ya'uduhu hifzuhuma, wa Huwal-'Aliyyul-'Azim.",
            mainContent = "আয়াতুল কুরসী কুরআনে কারীমের দ্বিতীয় সূরা আল-বাকারার ২৫৫তম আয়াত। এটি কুরআনের অন্যতম সম্মানিত ও মর্যাদাপূর্ণ আয়াত। রাসূলুল্লাহ (সা.) ইরশাদ করেছেন, যে ব্যক্তি প্রতি ফরজ সালাতের পর আয়াতুল কুরসী পাঠ করবে, তার জান্নাতে প্রবেশের পথে মৃত্যু ছাড়া আর কোনো বাধা থাকবে না (সুনানুন নাসাঈ)। রাতে শোয়ার সময় এটি পাঠ করলে আল্লাহ তাআলা একজন রক্ষক ফেরেশতা নিযুক্ত করেন এবং শয়তান সকাল পর্যন্ত তার নিকটবর্তী হতে পারে না।",
            categoryId = "cat_quran",
            subcategoryId = "sub_quran_verses",
            sourceReference = "Surah Al-Baqarah (2:255) / Sahih al-Bukhari & An-Nasa'i",
            author = "Noor Daily Editorial Board",
            tags = listOf("Quran", "Ayatul Kursi", "Al-Baqarah", "Daily Protection"),
            isFeatured = true,
            isPublished = true,
            viewCount = 1240L
        ),
        Post(
            id = "post_hadith_niyyah",
            title = "All Actions Depend on Intentions",
            titleBn = "কাজের ফলাফল নিয়তের ওপর নির্ভরশীল — সহীহ বুখারী",
            subtitle = "The opening Hadith of Sahih al-Bukhari emphasizing purity of intention",
            arabicText = "إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ، وَإِنَّمَا لِكُلِّ امْرِئٍ مَا نَوَى، فَمَنْ كَانَتْ هِجْرَتُهُ إِلَى دُنْيَا يُصِيبُهَا أَوْ إِلَى امْرَأَةٍ يَنْكِحُهَا فَهِجْرَتُهُ إِلَى مَا هَاجَرَ إِلَيْهِ",
            bengaliText = "নিশ্চয়ই সমস্ত কাজের ফলাফল নিয়তের ওপর নির্ভরশীল। প্রত্যেক ব্যক্তি তাই পাবে যার সে নিয়ত করে। সুতরাং যার হিজরত দুনিয়া অর্জনের উদ্দেশ্যে অথবা কোনো নারীকে বিবাহের উদ্দেশ্যে হয়, তবে তার হিজরত সেই উদ্দেশ্যের জন্যই গণ্য হবে যার দিকে সে হিজরত করেছে।",
            englishText = "The reward of deeds depends upon the intentions and every person will get the reward according to what he has intended. So whoever emigrated for worldly benefits or for a woman to marry, his emigration was for what he emigrated for.",
            transliteration = "Innamal-a'malu bin-niyyat, wa innama likulli-mri'im-ma nawa. Faman kanat hijratuhu ila dunya yusibuha awila-mra'atin yankihuha, fahijratuhu ila ma hajara ilayh.",
            mainContent = "আমীরুল মুমিনীন উমর ইবনুল খাত্তাব (রা.) থেকে বর্ণিত এই হাদিসটি ইসলামের ভিত্তিপ্রস্তর হিসেবে গণ্য করা হয়। ইমাম শাফেয়ী (রহ.) বলেন, এই হাদিসটি ইসলামের এক-তৃতীয়াংশ জ্ঞানের সমতুল্য। প্রতিটি ইবাদত—নামাজ, রোজা, সাদাকাহ এবং দ্বীনি শিক্ষা—আল্লাহর সন্তুষ্টির খাঁটি নিয়ত ছাড়া কবুল হয় না।",
            categoryId = "cat_hadith",
            subcategoryId = "sub_sahih_hadith",
            sourceReference = "Sahih al-Bukhari, Hadith 1; Sahih Muslim, Hadith 1907",
            author = "Imam al-Bukhari / Noor Daily Team",
            tags = listOf("Hadith", "Sahih Bukhari", "Niyyah", "Purity"),
            isFeatured = true,
            isPublished = true,
            viewCount = 890L
        ),
        Post(
            id = "post_tajweed_noon_sakin",
            title = "Rules of Noon Sakin and Tanween in Tajweed",
            titleBn = "নূন সাকিন ও তানভীনের চারটি মৌলিক নিয়ম",
            subtitle = "Izhar, Idgham, Iqlab, and Ikhfa explained with authentic recitation examples",
            arabicText = "مَنْ ءَامَنَ • مَن يَقُولُ • مِن بَعْدِ • مِن قَبْلُ",
            bengaliText = "কুরআন বিশুদ্ধভাবে তিলাওয়াতের জন্য নূন সাকিন (نْ) এবং তানভীন (ً ٍ ٌ)-এর হুকুম জানা অপরিহার্য। এর চারটি হুকুম রয়েছে: ১. ইযহার (স্পষ্ট করে পড়া), ২. ইদগাম (মিলিয়ে পড়া), ৩. ইক্বলাব (মীম দ্বারা পরিবর্তন করা), ৪. ইখফা (গুণ্নাহ সহ গোপন করা)।",
            englishText = "To recite the Quran with proper Tajweed, one must know the four rules of Noon Sakin and Tanween: Izhar (clear pronunciation), Idgham (merging), Iqlab (converting to Meem), and Ikhfa (concealing with nasal sound).",
            transliteration = "Man aamana (Izhar) • May-yaqoolu (Idgham) • Mim-ba'di (Iqlab) • Min qablu (Ikhfa)",
            mainContent = "১. ইযহারের হরফ ৬টি: ء هـ ع ح غ خ। নূন সাকিন বা তানভীনের পর এই হরফগুলো আসলে কোনো গুণ্নাহ ছাড়া স্পষ্ট পড়তে হয়।\n২. ইদগামের হরফ ৬টি (یرملون): য-র-ম-ল-ও-ন।\n৩. ইক্বলাবের হরফ ১টি: ب (বা)।\n৪. ইখফার হরফ ১৫টি: ت ث ج د ذ ز س ش ص ض ط ظ ف ق ك।",
            categoryId = "cat_quran",
            subcategoryId = "sub_tajweed_rules",
            sourceReference = "Tuhfat al-Atfal & Al-Jazariyyah Tajweed Manuals",
            author = "Tajweed Academy Noor",
            tags = listOf("Tajweed", "Noon Sakin", "Quran Recitation"),
            isFeatured = false,
            isPublished = true,
            viewCount = 760L
        ),
        Post(
            id = "post_sayyidul_istighfar",
            title = "Sayyidul Istighfar — The Master Supplication for Forgiveness",
            titleBn = "সাইয়্যিদুল ইস্তিগফার — তওবা ও ক্ষমা প্রার্থনার শ্রেষ্ঠতম দুআ",
            subtitle = "The highest form of seeking forgiveness taught directly by Prophet Muhammad (pbuh)",
            arabicText = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ لَكَ بِذَنْبِي فَاغْفِرْ لِي، فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ",
            bengaliText = "হে আল্লাহ! আপনি আমার রব, আপনি ছাড়া সত্য কোনো ইলাহ নেই। আপনি আমাকে সৃষ্টি করেছেন এবং আমি আপনার বান্দা। আমি আমার সাধ্যানুযায়ী আপনার অঙ্গীকার ও প্রতিশ্রুতির ওপর আছি। আমি আমার কৃতকর্মের মন্দ থেকে আপনার আশ্রয় প্রার্থনা করছি। আমার ওপর আপনার যে নিয়ামত রয়েছে তা স্বীকার করছি এবং আমার অপরাধসমূহ স্বীকার করছি। অতএব আপনি আমাকে ক্ষমা করে দিন; কারণ আপনি ছাড়া গুনাহ মাফ করার আর কেউ নেই।",
            englishText = "O Allah, You are my Lord; there is no deity except You. You created me and I am Your servant, and I abide by Your covenant and promise as best I can. I seek refuge in You from the evil of what I have done. I acknowledge before You Your blessings upon me, and I confess my sins to You. So forgive me, for none forgives sins except You.",
            transliteration = "Allahumma Anta Rabbi, la ilaha illa Anta, khalaqtani wa ana 'abduka, wa ana 'ala 'ahdika wa wa'dika mastata'tu, a'udhu bika min sharri ma sana'tu, abu'u laka bini'matika 'alayya, wa abu'u laka bidhanbi, faghfir li, fa-innahu la yaghfirudh-dhunuba illa Anta.",
            mainContent = "শাদ্দাদ ইবনে আওস (রা.) থেকে বর্ণিত, নবীজী (সা.) ইরশাদ করেন: যে ব্যক্তি দিনের বেলায় দৃঢ় বিশ্বাসের সাথে এ দুআটি পড়বে এবং সন্ধ্যার আগেই মারা যাবে, সে জান্নাতীদের অন্তর্ভুক্ত হবে। আর যে ব্যক্তি রাতে এ দুআ পড়বে এবং সকাল হওয়ার আগেই মারা যাবে, সেও জান্নাতী হবে। (সহীহ বুখারী, হাদিস নং ৬৩০৬)।",
            categoryId = "cat_dua",
            subcategoryId = "sub_morning_evening",
            sourceReference = "Sahih al-Bukhari, Hadith 6306",
            author = "Noor Daily Editorial",
            tags = listOf("Dua", "Istighfar", "Forgiveness", "Bukhari"),
            isFeatured = true,
            isPublished = true,
            viewCount = 1530L
        ),
        Post(
            id = "post_kids_kalima_tayyibah",
            title = "First Kalima Tayyibah for Children",
            titleBn = "কালিমা তায়্যিবাহ — শিশুদের জন্য সহজ শিক্ষা ও অর্থ",
            subtitle = "The foundational testimony of Islamic faith for young learners",
            arabicText = "لَا إِلَٰهَ إِلَّا اللَّهُ مُحَمَّدٌ رَّسُولُ اللَّهِ",
            bengaliText = "আল্লাহ ছাড়া কোনো সত্য উপাস্য নেই, মুহাম্মদ (সা.) আল্লাহর রাসূল।",
            englishText = "There is no deity worthy of worship except Allah, and Muhammad is the Messenger of Allah.",
            transliteration = "La ilaha illallahu Muhammadur Rasulullah.",
            mainContent = "কালিমা তায়্যিবাহ হলো ইসলামের প্রধান প্রবেশদ্বার। প্রতিটি মুসলিম শিশুর হৃদয়ে এই কালিমার বিশ্বাস এবং ভালোবাসা ছোটবেলা থেকেই প্রোথিত করা পিতা-মাতার পরম দায়িত্ব। এর প্রথম অংশে তাওহীদের সাক্ষ্য এবং দ্বিতীয় অংশে রিসালাতের সাক্ষ্য দেয়া হয়।",
            categoryId = "cat_kids",
            subcategoryId = "sub_kids_kalima",
            sourceReference = "Fundamental Creed of Islam (Aqeedah Tahawiyyah)",
            author = "Noor Kids Department",
            tags = listOf("Kids", "Kalima", "Faith", "Tawheed"),
            isFeatured = false,
            isPublished = true,
            viewCount = 620L
        ),
        Post(
            id = "post_morning_azkar_dua",
            title = "Authentic Morning Remembrance (Dua for Morning)",
            titleBn = "সকাল বেলার গুরুত্বপূর্ণ আজকার ও বরকত লাভের দুআ",
            subtitle = "Protecting your day with the remembrance of Allah and surrender to His Kingdom",
            arabicText = "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لاَ إِلَهَ إِلاَّ اللَّهُ وَحْدَهُ لاَ شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
            bengaliText = "আমরা সকালে উপনীত হয়েছি এবং সমস্ত রাজত্বও আল্লাহর জন্য সকালে উপনীত হয়েছে। সমস্ত প্রশংসা আল্লাহর। আল্লাহ ছাড়া কোনো সত্য উপাস্য নেই, তিনি একক, তাঁর কোনো অংশীদার নেই। রাজত্ব একমাত্র তাঁরই, প্রশংসাও তাঁর এবং তিনি সকল বিষয়ের ওপর সর্বশক্তিমান।",
            englishText = "We have entered upon the morning and the kingdom belongs to Allah, all praise is due to Allah, there is no god but Allah alone, Who has no partner. To Him belongs the dominion and to Him belongs the praise, and He has power over all things.",
            transliteration = "Asbahna wa asbahal-mulku lillah, wal-hamdu lillah, la ilaha illallahu wahdahu la sharika lah, lahul-mulku wa lahul-hamdu wa Huwa 'ala kulli shay'in Qadir.",
            mainContent = "আবদুল্লাহ ইবনে মাসউদ (রা.) থেকে বর্ণিত, রাসূলুল্লাহ (সা.) যখন সকাল করতেন তখন এই দুআটি পাঠ করতেন। এটি দিন শুরুর অন্যতম শ্রেষ্ঠ আমল যা একজন মুমিনকে সারাদিন আল্লাহর রহমত ও বরকতের ছায়াতলে রাখে। (সহীহ মুসলিম)।",
            categoryId = "cat_dua",
            subcategoryId = "sub_morning_evening",
            sourceReference = "Sahih Muslim, Hadith 2723",
            author = "Noor Daily Editorial",
            tags = listOf("Dua", "Morning Azkar", "Sahih Muslim"),
            isFeatured = false,
            isPublished = true,
            viewCount = 940L
        )
    )

    val quizzes = listOf(
        Quiz(
            id = "quiz_quran_basics",
            title = "Quran & Tajweed Knowledge Quiz",
            titleBn = "পবিত্র কুরআন ও তাজবীদ জ্ঞান কুইজ",
            description = "৫টি গুরুত্বপূর্ণ প্রশ্নের মাধ্যমে আপনার কুরআন সম্পর্কিত সাধারণ জ্ঞান পরীক্ষা করুন।",
            categoryId = "cat_quran",
            questionCount = 5,
            isActive = true
        ),
        Quiz(
            id = "quiz_hadith_basics",
            title = "Hadith & Sunnah Quiz",
            titleBn = "হাদিস ও সুন্নাহ ভিত্তিক কুইজ",
            description = "সহীহ হাদিসের গ্রন্থাবলী এবং নবীজীর বাণী সম্পর্কিত কুইজ।",
            categoryId = "cat_hadith",
            questionCount = 5,
            isActive = true
        )
    )

    val quizQuestions = listOf(
        // Quran quiz questions
        QuizQuestion(
            id = "q_quran_1",
            quizId = "quiz_quran_basics",
            question = "Which Surah is known as the 'Heart of the Quran' (Qalb al-Quran)?",
            questionBn = "পবিত্র কুরআনের কোন সূরাকে 'কুরআনের হৃদয়' (ক্বলবুল কুরআন) বলা হয়?",
            options = listOf("Surah Al-Baqarah", "Surah Ya-Sin", "Surah Al-Mulk", "Surah Al-Rahman"),
            optionsBn = listOf("সূরা আল-বাকারা", "সূরা ইয়াসীন", "সূরা আল-মুলক", "সূরা আর-রহমান"),
            correctAnswerIndex = 1,
            explanation = "রাসূলুল্লাহ (সা.) ইরশাদ করেছেন, প্রত্যেক বস্তুর একটি হৃদয় রয়েছে, আর কুরআনের হৃদয় হলো সূরা ইয়াসীন (তিরমিযী)।"
        ),
        QuizQuestion(
            id = "q_quran_2",
            quizId = "quiz_quran_basics",
            question = "How many Surahs are there in the Holy Quran?",
            questionBn = "পবিত্র কুরআনে সর্বমোট কতটি সূরা রয়েছে?",
            options = listOf("110", "112", "114", "120"),
            optionsBn = listOf("১১০টি", "১১২টি", "১১৪টি", "১২০টি"),
            correctAnswerIndex = 2,
            explanation = "পবিত্র কুরআনে সর্বমোট ১১৪টি পূর্ণাঙ্গ সূরা রয়েছে।"
        ),
        QuizQuestion(
            id = "q_quran_3",
            quizId = "quiz_quran_basics",
            question = "What is the longest Surah in the Holy Quran?",
            questionBn = "পবিত্র কুরআনের সবচেয়ে দীর্ঘতম সূরা কোনটি?",
            options = listOf("Surah Al-Imran", "Surah Al-Baqarah", "Surah An-Nisa", "Surah Al-Maidah"),
            optionsBn = listOf("সূরা আলে ইমরান", "সূরা আল-বাকারা", "সূরা আন-নিসা", "সূরা আল-মায়েদা"),
            correctAnswerIndex = 1,
            explanation = "সূরা আল-বাকারা কুরআনের দীর্ঘতম সূরা, যাতে মোট ২৮৬টি আয়াত রয়েছে।"
        ),
        QuizQuestion(
            id = "q_quran_4",
            quizId = "quiz_quran_basics",
            question = "How many throat letters (Huroof Halqiyyah) are there in Tajweed for Izhar?",
            questionBn = "তাজবীদের ইযহারের নিয়মে কণ্ঠনালীর হরফ (হরুফে হলক্বী) কয়টি?",
            options = listOf("4", "5", "6", "8"),
            optionsBn = listOf("৪টি", "৫টি", "৬টি", "৮টি"),
            correctAnswerIndex = 2,
            explanation = "হরুফে হলক্বী ৬টি: ء هـ ع ح غ خ। নূন সাকিন বা তানভীনের পর এই হরফ আসলে ইযহার (স্পষ্ট) করে পড়তে হয়।"
        ),
        QuizQuestion(
            id = "q_quran_5",
            quizId = "quiz_quran_basics",
            question = "Which Surah does not begin with 'Bismillah ir-Rahman ir-Rahim'?",
            questionBn = "পবিত্র কুরআনের কোন সূরার শুরুতে 'বিসমিল্লাহ' নেই?",
            options = listOf("Surah At-Tawbah", "Surah Al-Anfal", "Surah Al-Kahf", "Surah Maryam"),
            optionsBn = listOf("সূরা আত-তাওবাহ", "সূরা আল-আনফাল", "সূরা আল-কাহাফ", "সূরা মারইয়াম"),
            correctAnswerIndex = 0,
            explanation = "সূরা আত-তাওবার শুরুতে বিসমিল্লাহ নেই।"
        ),
        // Hadith quiz questions
        QuizQuestion(
            id = "q_hadith_1",
            quizId = "quiz_hadith_basics",
            question = "Who compiled the most authentic book of Hadith, Sahih al-Bukhari?",
            questionBn = "সর্বাধিক বিশুদ্ধ হাদিস গ্রন্থ 'সহীহ বুখারী' সংকলন করেন কোন মহান ইমাম?",
            options = listOf("Imam Muslim", "Imam Muhammad ibn Ismail al-Bukhari", "Imam Abu Hanifah", "Imam Malik"),
            optionsBn = listOf("ইমাম মুসলিম", "ইমাম মুহাম্মদ ইবনে ইসমাইল বুখারী", "ইমাম আবু হানিফা", "ইমাম মালিক"),
            correctAnswerIndex = 1,
            explanation = "ইমাম আবু আবদুল্লাহ মুহাম্মদ ইবনে ইসমাইল আল-বুখারী (রহ.) দীর্ঘ ১৬ বছর সাধনা করে এটি সংকলন করেন।"
        ),
        QuizQuestion(
            id = "q_hadith_2",
            quizId = "quiz_hadith_basics",
            question = "Which companion of the Prophet narrated the highest number of Hadith?",
            questionBn = "কোন সম্মানিত সাহাবী সর্বাধিক হাদিস বর্ণনা করেছেন?",
            options = listOf("Umar ibn al-Khattab (RA)", "Abu Bakr as-Siddiq (RA)", "Abu Hurairah (RA)", "Ali ibn Abi Talib (RA)"),
            optionsBn = listOf("উমর ইবনুল খাত্তাব (রা.)", "আবু বকর আস-সিদ্দিক (রা.)", "আবু হুরায়রা (রা.)", "আলী ইবনে আবি তালিব (রা.)"),
            correctAnswerIndex = 2,
            explanation = "হযরত আবু হুরায়রা (রা.) ৫,৩৭৪টি হাদিস বর্ণনা করেছেন।"
        )
    )
}
