package com.example.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.acos
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.tan

data class WorldLocation(
    val id: String,
    val countryBn: String,
    val countryEn: String,
    val cityBn: String,
    val cityEn: String,
    val timezoneId: String,
    val flagEmoji: String,
    val lat: Double,
    val lng: Double,
    val region: String,
    val isDefault: Boolean = false
)

data class HijriDateResult(
    val day: Int,
    val month: Int,
    val year: Int,
    val dayBn: String,
    val dayAr: String,
    val monthNameBn: String,
    val monthNameAr: String,
    val monthNameEn: String,
    val yearBn: String,
    val yearAr: String,
    val formattedFullBn: String,
    val formattedFullAr: String,
    val formattedFullEn: String
)

data class EnglishDateResult(
    val formattedFullEn: String,
    val formattedFullBn: String,
    val dayNameEn: String,
    val dayNameBn: String,
    val dateString: String
)

data class LiveTimeResult(
    val time12En: String,
    val time12Bn: String,
    val hour12: Int,
    val minute: Int,
    val second: Int,
    val isPm: Boolean,
    val amPmString: String,
    val timezoneOffsetString: String
)

data class PrayerTimeItem(
    val id: String,
    val nameBn: String,
    val nameEn: String,
    val nameAr: String,
    val timeFormatted: String,
    val endTimeFormatted: String = "",
    val rakatsDescription: String = "",
    val hadithVirtue: String = "",
    val isCurrent: Boolean = false,
    val isNext: Boolean = false
)

data class ForbiddenTimeItem(
    val titleBn: String,
    val periodFormatted: String,
    val descriptionBn: String
)

data class LocationPrayerTimes(
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String,
    val tahajjudEnd: String,
    val ishraqStart: String,
    val nextPrayerNameBn: String,
    val nextPrayerRemaining: String,
    val nextPrayerTimeFormatted: String = "",
    val nextPrayerRemainingCompact: String = "",
    val allPrayers: List<PrayerTimeItem>,
    val forbiddenTimes: List<ForbiddenTimeItem>
)

object IslamicTimeManager {

    val DEFAULT_LOCATION = WorldLocation(
        id = "bd_dhaka",
        countryBn = "বাংলাদেশ",
        countryEn = "Bangladesh",
        cityBn = "ঢাকা",
        cityEn = "Dhaka",
        timezoneId = "Asia/Dhaka",
        flagEmoji = "🇧🇩",
        lat = 23.8103,
        lng = 90.4125,
        region = "বাংলাদেশ (ঢাকা বিভাগ)",
        isDefault = true
    )

    // Complete coverage of all 64 districts in Bangladesh + Middle East + Global Hubs
    val POPULAR_LOCATIONS: List<WorldLocation> = listOf(
        // --- বাংলাদেশ: ঢাকা বিভাগ ---
        DEFAULT_LOCATION,
        WorldLocation("bd_gazipur", "বাংলাদেশ", "Bangladesh", "গাজীপুর", "Gazipur", "Asia/Dhaka", "🇧🇩", 23.9999, 90.4203, "বাংলাদেশ (ঢাকা বিভাগ)"),
        WorldLocation("bd_narayanganj", "বাংলাদেশ", "Bangladesh", "নারায়ণগঞ্জ", "Narayanganj", "Asia/Dhaka", "🇧🇩", 23.6238, 90.5000, "বাংলাদেশ (ঢাকা বিভাগ)"),
        WorldLocation("bd_tangail", "বাংলাদেশ", "Bangladesh", "টাঙ্গাইল", "Tangail", "Asia/Dhaka", "🇧🇩", 24.2513, 89.9167, "বাংলাদেশ (ঢাকা বিভাগ)"),
        WorldLocation("bd_faridpur", "বাংলাদেশ", "Bangladesh", "ফরিদপুর", "Faridpur", "Asia/Dhaka", "🇧🇩", 23.6071, 89.8426, "বাংলাদেশ (ঢাকা বিভাগ)"),
        WorldLocation("bd_manikganj", "বাংলাদেশ", "Bangladesh", "মানিকগঞ্জ", "Manikganj", "Asia/Dhaka", "🇧🇩", 23.8617, 90.0003, "বাংলাদেশ (ঢাকা বিভাগ)"),
        WorldLocation("bd_munshiganj", "বাংলাদেশ", "Bangladesh", "মুন্সীগঞ্জ", "Munshiganj", "Asia/Dhaka", "🇧🇩", 23.5422, 90.5305, "বাংলাদেশ (ঢাকা বিভাগ)"),
        WorldLocation("bd_narsingdi", "বাংলাদেশ", "Bangladesh", "নরসিংদী", "Narsingdi", "Asia/Dhaka", "🇧🇩", 23.9193, 90.7202, "বাংলাদেশ (ঢাকা বিভাগ)"),
        WorldLocation("bd_kishoreganj", "বাংলাদেশ", "Bangladesh", "কিশোরগঞ্জ", "Kishoreganj", "Asia/Dhaka", "🇧🇩", 24.4449, 90.7766, "বাংলাদেশ (ঢাকা বিভাগ)"),
        WorldLocation("bd_gopalganj", "বাংলাদেশ", "Bangladesh", "গোপালগঞ্জ", "Gopalganj", "Asia/Dhaka", "🇧🇩", 23.0051, 89.8266, "বাংলাদেশ (ঢাকা বিভাগ)"),
        WorldLocation("bd_madaripur", "বাংলাদেশ", "Bangladesh", "মাদারীপুর", "Madaripur", "Asia/Dhaka", "🇧🇩", 23.1641, 90.1897, "বাংলাদেশ (ঢাকা বিভাগ)"),
        WorldLocation("bd_rajbari", "বাংলাদেশ", "Bangladesh", "রাজবাড়ী", "Rajbari", "Asia/Dhaka", "🇧🇩", 23.7574, 89.6445, "বাংলাদেশ (ঢাকা বিভাগ)"),
        WorldLocation("bd_shariatpur", "বাংলাদেশ", "Bangladesh", "শরীয়তপুর", "Shariatpur", "Asia/Dhaka", "🇧🇩", 23.2423, 90.4348, "বাংলাদেশ (ঢাকা বিভাগ)"),

        // --- বাংলাদেশ: চট্টগ্রাম বিভাগ ---
        WorldLocation("bd_ctg", "বাংলাদেশ", "Bangladesh", "চট্টগ্রাম", "Chittagong", "Asia/Dhaka", "🇧🇩", 22.3569, 91.7832, "বাংলাদেশ (চট্টগ্রাম বিভাগ)"),
        WorldLocation("bd_coxsbazar", "বাংলাদেশ", "Bangladesh", "কক্সবাজার", "Cox's Bazar", "Asia/Dhaka", "🇧🇩", 21.4272, 92.0058, "বাংলাদেশ (চট্টগ্রাম বিভাগ)"),
        WorldLocation("bd_comilla", "বাংলাদেশ", "Bangladesh", "কুমিল্লা", "Cumilla", "Asia/Dhaka", "🇧🇩", 23.4682, 91.1788, "বাংলাদেশ (চট্টগ্রাম বিভাগ)"),
        WorldLocation("bd_brahmanbaria", "বাংলাদেশ", "Bangladesh", "ব্রাহ্মণবাড়িয়া", "Brahmanbaria", "Asia/Dhaka", "🇧🇩", 23.9571, 91.1119, "বাংলাদেশ (চট্টগ্রাম বিভাগ)"),
        WorldLocation("bd_chandpur", "বাংলাদেশ", "Bangladesh", "চাঁদপুর", "Chandpur", "Asia/Dhaka", "🇧🇩", 23.2333, 90.6712, "বাংলাদেশ (চট্টগ্রাম বিভাগ)"),
        WorldLocation("bd_feni", "বাংলাদেশ", "Bangladesh", "ফেনী", "Feni", "Asia/Dhaka", "🇧🇩", 23.0159, 91.3976, "বাংলাদেশ (চট্টগ্রাম বিভাগ)"),
        WorldLocation("bd_noakhali", "বাংলাদেশ", "Bangladesh", "নোয়াখালী", "Noakhali", "Asia/Dhaka", "🇧🇩", 22.8696, 91.0994, "বাংলাদেশ (চট্টগ্রাম বিভাগ)"),
        WorldLocation("bd_lakshmipur", "বাংলাদেশ", "Bangladesh", "লক্ষ্মীপুর", "Lakshmipur", "Asia/Dhaka", "🇧🇩", 22.9425, 90.8412, "বাংলাদেশ (চট্টগ্রাম বিভাগ)"),
        WorldLocation("bd_rangamati", "বাংলাদেশ", "Bangladesh", "রাঙ্গামাটি", "Rangamati", "Asia/Dhaka", "🇧🇩", 22.7324, 92.2985, "বাংলাদেশ (চট্টগ্রাম বিভাগ)"),
        WorldLocation("bd_bandarban", "বাংলাদেশ", "Bangladesh", "বান্দরবান", "Bandarban", "Asia/Dhaka", "🇧🇩", 22.1953, 92.2184, "বাংলাদেশ (চট্টগ্রাম বিভাগ)"),
        WorldLocation("bd_khagrachhari", "বাংলাদেশ", "Bangladesh", "খাগড়াছড়ি", "Khagrachhari", "Asia/Dhaka", "🇧🇩", 23.1193, 91.9847, "বাংলাদেশ (চট্টগ্রাম বিভাগ)"),

        // --- বাংলাদেশ: সিলেট বিভাগ ---
        WorldLocation("bd_syl", "বাংলাদেশ", "Bangladesh", "সিলেট", "Sylhet", "Asia/Dhaka", "🇧🇩", 24.8949, 91.8687, "বাংলাদেশ (সিলেট বিভাগ)"),
        WorldLocation("bd_moulvibazar", "বাংলাদেশ", "Bangladesh", "মৌলভীবাজার", "Moulvibazar", "Asia/Dhaka", "🇧🇩", 24.4829, 91.7774, "বাংলাদেশ (সিলেট বিভাগ)"),
        WorldLocation("bd_habiganj", "বাংলাদেশ", "Bangladesh", "হবিগঞ্জ", "Habiganj", "Asia/Dhaka", "🇧🇩", 24.3749, 91.4155, "বাংলাদেশ (সিলেট বিভাগ)"),
        WorldLocation("bd_sunamganj", "বাংলাদেশ", "Bangladesh", "সুনামগঞ্জ", "Sunamganj", "Asia/Dhaka", "🇧🇩", 25.0658, 91.3950, "বাংলাদেশ (সিলেট বিভাগ)"),

        // --- বাংলাদেশ: রাজশাহী বিভাগ ---
        WorldLocation("bd_raj", "বাংলাদেশ", "Bangladesh", "রাজশাহী", "Rajshahi", "Asia/Dhaka", "🇧🇩", 24.3636, 88.6241, "বাংলাদেশ (রাজশাহী বিভাগ)"),
        WorldLocation("bd_bogra", "বাংলাদেশ", "Bangladesh", "বগুড়া", "Bogura", "Asia/Dhaka", "🇧🇩", 24.8465, 89.3777, "বাংলাদেশ (রাজশাহী বিভাগ)"),
        WorldLocation("bd_pabna", "বাংলাদেশ", "Bangladesh", "পাবনা", "Pabna", "Asia/Dhaka", "🇧🇩", 24.0064, 89.2372, "বাংলাদেশ (রাজশাহী বিভাগ)"),
        WorldLocation("bd_sirajganj", "বাংলাদেশ", "Bangladesh", "সিরাজগঞ্জ", "Sirajganj", "Asia/Dhaka", "🇧🇩", 24.4534, 89.7006, "বাংলাদেশ (রাজশাহী বিভাগ)"),
        WorldLocation("bd_naogaon", "বাংলাদেশ", "Bangladesh", "নওগাঁ", "Naogaon", "Asia/Dhaka", "🇧🇩", 24.7936, 88.9318, "বাংলাদেশ (রাজশাহী বিভাগ)"),
        WorldLocation("bd_natore", "বাংলাদেশ", "Bangladesh", "নাটোর", "Natore", "Asia/Dhaka", "🇧🇩", 24.4206, 88.9866, "বাংলাদেশ (রাজশাহী বিভাগ)"),
        WorldLocation("bd_chapai", "বাংলাদেশ", "Bangladesh", "চাঁপাইনবাবগঞ্জ", "Chapai Nawabganj", "Asia/Dhaka", "🇧🇩", 24.5965, 88.2775, "বাংলাদেশ (রাজশাহী বিভাগ)"),
        WorldLocation("bd_joypurhat", "বাংলাদেশ", "Bangladesh", "জয়পুরহাট", "Joypurhat", "Asia/Dhaka", "🇧🇩", 25.0968, 89.0227, "বাংলাদেশ (রাজশাহী বিভাগ)"),

        // --- বাংলাদেশ: খুলনা বিভাগ ---
        WorldLocation("bd_khulna", "বাংলাদেশ", "Bangladesh", "খুলনা", "Khulna", "Asia/Dhaka", "🇧🇩", 22.8456, 89.5403, "বাংলাদেশ (খুলনা বিভাগ)"),
        WorldLocation("bd_jessore", "বাংলাদেশ", "Bangladesh", "যশোর", "Jashore", "Asia/Dhaka", "🇧🇩", 23.1664, 89.2081, "বাংলাদেশ (খুলনা বিভাগ)"),
        WorldLocation("bd_kushtia", "বাংলাদেশ", "Bangladesh", "কুষ্টিয়া", "Kushtia", "Asia/Dhaka", "🇧🇩", 23.9013, 89.1204, "বাংলাদেশ (খুলনা বিভাগ)"),
        WorldLocation("bd_satkhira", "বাংলাদেশ", "Bangladesh", "সাতক্ষীরা", "Satkhira", "Asia/Dhaka", "🇧🇩", 22.7185, 89.0705, "বাংলাদেশ (খুলনা বিভাগ)"),
        WorldLocation("bd_bagerhat", "বাংলাদেশ", "Bangladesh", "বাগেরহাট", "Bagerhat", "Asia/Dhaka", "🇧🇩", 22.6516, 89.7859, "বাংলাদেশ (খুলনা বিভাগ)"),
        WorldLocation("bd_jhenaidah", "বাংলাদেশ", "Bangladesh", "ঝিনাইদহ", "Jhenaidah", "Asia/Dhaka", "🇧🇩", 23.5448, 89.1539, "বাংলাদেশ (খুলনা বিভাগ)"),
        WorldLocation("bd_chuadanga", "বাংলাদেশ", "Bangladesh", "চুয়াডাঙ্গা", "Chuadanga", "Asia/Dhaka", "🇧🇩", 23.6402, 88.8418, "বাংলাদেশ (খুলনা বিভাগ)"),
        WorldLocation("bd_magura", "বাংলাদেশ", "Bangladesh", "মাগুরা", "Magura", "Asia/Dhaka", "🇧🇩", 23.4873, 89.4198, "বাংলাদেশ (খুলনা বিভাগ)"),
        WorldLocation("bd_narail", "বাংলাদেশ", "Bangladesh", "নড়াইল", "Narail", "Asia/Dhaka", "🇧🇩", 23.1725, 89.5127, "বাংলাদেশ (খুলনা বিভাগ)"),
        WorldLocation("bd_meherpur", "বাংলাদেশ", "Bangladesh", "মেহেরপুর", "Meherpur", "Asia/Dhaka", "🇧🇩", 23.7622, 88.6318, "বাংলাদেশ (খুলনা বিভাগ)"),

        // --- বাংলাদেশ: বরিশাল বিভাগ ---
        WorldLocation("bd_barishal", "বাংলাদেশ", "Bangladesh", "বরিশাল", "Barishal", "Asia/Dhaka", "🇧🇩", 22.7010, 90.3535, "বাংলাদেশ (বরিশাল বিভাগ)"),
        WorldLocation("bd_bhola", "বাংলাদেশ", "Bangladesh", "ভোলা", "Bhola", "Asia/Dhaka", "🇧🇩", 22.6859, 90.6481, "বাংলাদেশ (বরিশাল বিভাগ)"),
        WorldLocation("bd_patuakhali", "বাংলাদেশ", "Bangladesh", "পটুয়াখালী", "Patuakhali", "Asia/Dhaka", "🇧🇩", 22.3596, 90.3299, "বাংলাদেশ (বরিশাল বিভাগ)"),
        WorldLocation("bd_pirojpur", "বাংলাদেশ", "Bangladesh", "পিরোজপুর", "Pirojpur", "Asia/Dhaka", "🇧🇩", 22.5841, 89.9720, "বাংলাদেশ (বরিশাল বিভাগ)"),
        WorldLocation("bd_barguna", "বাংলাদেশ", "Bangladesh", "বরগুনা", "Barguna", "Asia/Dhaka", "🇧🇩", 22.0953, 90.1121, "বাংলাদেশ (বরিশাল বিভাগ)"),
        WorldLocation("bd_jhalokati", "বাংলাদেশ", "Bangladesh", "ঝালকাঠি", "Jhalokati", "Asia/Dhaka", "🇧🇩", 22.6406, 90.1987, "বাংলাদেশ (বরিশাল বিভাগ)"),

        // --- বাংলাদেশ: রংপুর বিভাগ ---
        WorldLocation("bd_rangpur", "বাংলাদেশ", "Bangladesh", "রংপুর", "Rangpur", "Asia/Dhaka", "🇧🇩", 25.7439, 89.2752, "বাংলাদেশ (রংপুর বিভাগ)"),
        WorldLocation("bd_dinajpur", "বাংলাদেশ", "Bangladesh", "দিনাজপুর", "Dinajpur", "Asia/Dhaka", "🇧🇩", 25.6217, 88.6355, "বাংলাদেশ (রংপুর বিভাগ)"),
        WorldLocation("bd_kurigram", "বাংলাদেশ", "Bangladesh", "কুড়িগ্রাম", "Kurigram", "Asia/Dhaka", "🇧🇩", 25.8054, 89.6362, "বাংলাদেশ (রংপুর বিভাগ)"),
        WorldLocation("bd_gaibandha", "বাংলাদেশ", "Bangladesh", "গাইবান্ধা", "Gaibandha", "Asia/Dhaka", "🇧🇩", 25.3288, 89.5430, "বাংলাদেশ (রংপুর বিভাগ)"),
        WorldLocation("bd_nilphamari", "বাংলাদেশ", "Bangladesh", "নীলফামারী", "Nilphamari", "Asia/Dhaka", "🇧🇩", 25.9318, 88.8560, "বাংলাদেশ (রংপুর বিভাগ)"),
        WorldLocation("bd_thakurgaon", "বাংলাদেশ", "Bangladesh", "ঠাকুরগাঁও", "Thakurgaon", "Asia/Dhaka", "🇧🇩", 26.0337, 88.4617, "বাংলাদেশ (রংপুর বিভাগ)"),
        WorldLocation("bd_panchagarh", "বাংলাদেশ", "Bangladesh", "পঞ্চগড়", "Panchagarh", "Asia/Dhaka", "🇧🇩", 26.3411, 88.5542, "বাংলাদেশ (রংপুর বিভাগ)"),
        WorldLocation("bd_lalmonirhat", "বাংলাদেশ", "Bangladesh", "লালমনিরহাট", "Lalmonirhat", "Asia/Dhaka", "🇧🇩", 25.9923, 89.2847, "বাংলাদেশ (রংপুর বিভাগ)"),

        // --- বাংলাদেশ: ময়মনসিংহ বিভাগ ---
        WorldLocation("bd_mymensingh", "বাংলাদেশ", "Bangladesh", "ময়মনসিংহ", "Mymensingh", "Asia/Dhaka", "🇧🇩", 24.7471, 90.4203, "বাংলাদেশ (ময়মনসিংহ বিভাগ)"),
        WorldLocation("bd_jamalpur", "বাংলাদেশ", "Bangladesh", "জামালপুর", "Jamalpur", "Asia/Dhaka", "🇧🇩", 24.9375, 89.9378, "বাংলাদেশ (ময়মনসিংহ বিভাগ)"),
        WorldLocation("bd_sherpur", "বাংলাদেশ", "Bangladesh", "শেরপুর", "Sherpur", "Asia/Dhaka", "🇧🇩", 25.0205, 90.0153, "বাংলাদেশ (ময়মনসিংহ বিভাগ)"),
        WorldLocation("bd_netrokona", "বাংলাদেশ", "Bangladesh", "নেত্রকোণা", "Netrokona", "Asia/Dhaka", "🇧🇩", 24.8709, 90.7279, "বাংলাদেশ (ময়মনসিংহ বিভাগ)"),

        // --- মধ্যপ্রাচ্য (Middle East) ---
        WorldLocation("sa_makkah", "সৌদি আরব", "Saudi Arabia", "পবিত্র মক্কা", "Makkah", "Asia/Riyadh", "🇸🇦", 21.4225, 39.8262, "মধ্যপ্রাচ্য"),
        WorldLocation("sa_madinah", "সৌদি আরব", "Saudi Arabia", "পবিত্র মদিনা", "Madinah", "Asia/Riyadh", "🇸🇦", 24.5247, 39.5692, "মধ্যপ্রাচ্য"),
        WorldLocation("sa_riyadh", "সৌদি আরব", "Saudi Arabia", "রিয়াদ", "Riyadh", "Asia/Riyadh", "🇸🇦", 24.7136, 46.6753, "মধ্যপ্রাচ্য"),
        WorldLocation("ae_dubai", "সংযুক্ত আরব আমিরাত", "UAE", "দুবাই", "Dubai", "Asia/Dubai", "🇦🇪", 25.2048, 55.2708, "মধ্যপ্রাচ্য"),
        WorldLocation("ae_abudhabi", "সংযুক্ত আরব আমিরাত", "UAE", "আবুধাবি", "Abu Dhabi", "Asia/Dubai", "🇦🇪", 24.4539, 54.3773, "মধ্যপ্রাচ্য"),
        WorldLocation("qa_doha", "কাতার", "Qatar", "দোহা", "Doha", "Asia/Qatar", "🇶🇦", 25.2854, 51.5310, "মধ্যপ্রাচ্য"),
        WorldLocation("kw_kuwait", "কুয়েত", "Kuwait", "কুয়েত সিটি", "Kuwait City", "Asia/Kuwait", "🇰🇼", 29.3759, 47.9774, "মধ্যপ্রাচ্য"),
        WorldLocation("om_muscat", "ওমান", "Oman", "মাস্কাট", "Muscat", "Asia/Muscat", "🇴🇲", 23.5880, 58.3829, "মধ্যপ্রাচ্য"),
        WorldLocation("bh_manama", "বাহরাইন", "Bahrain", "মানামা", "Manama", "Asia/Bahrain", "🇧🇭", 26.2285, 50.5860, "মধ্যপ্রাচ্য"),
        WorldLocation("tr_istanbul", "তুরস্ক", "Turkey", "ইস্তাম্বুল", "Istanbul", "Europe/Istanbul", "🇹🇷", 41.0082, 28.9784, "মধ্যপ্রাচ্য"),
        WorldLocation("eg_cairo", "মিশর", "Egypt", "কায়রো", "Cairo", "Africa/Cairo", "🇪🇬", 30.0444, 31.2357, "মধ্যপ্রাচ্য"),
        WorldLocation("jo_amman", "জর্ডান", "Jordan", "আম্মান", "Amman", "Asia/Amman", "🇯🇴", 31.9454, 35.9284, "মধ্যপ্রাচ্য"),
        WorldLocation("ps_jerusalem", "ফিলিস্তিন", "Palestine", "বায়তুল মুকাদ্দাস", "Jerusalem", "Asia/Jerusalem", "🇵🇸", 31.7683, 35.2137, "মধ্যপ্রাচ্য"),

        // --- এশিয়া ও দক্ষিণ এশিয়া ---
        WorldLocation("in_kolkata", "ভারত", "India", "কলকাতা", "Kolkata", "Asia/Kolkata", "🇮🇳", 22.5726, 88.3639, "দক্ষিণ ও পূর্ব এশিয়া"),
        WorldLocation("in_delhi", "ভারত", "India", "দিল্লি", "New Delhi", "Asia/Kolkata", "🇮🇳", 28.6139, 77.2090, "দক্ষিণ ও পূর্ব এশিয়া"),
        WorldLocation("pk_karachi", "পাকিস্তান", "Pakistan", "করাচি", "Karachi", "Asia/Karachi", "🇵🇰", 24.8607, 67.0011, "দক্ষিণ ও পূর্ব এশিয়া"),
        WorldLocation("my_kl", "মালয়েশিয়া", "Malaysia", "কুয়ালালামপুর", "Kuala Lumpur", "Asia/Kuala_Lumpur", "🇲🇾", 3.1390, 101.6869, "দক্ষিণ ও পূর্ব এশিয়া"),
        WorldLocation("sg_singapore", "সিঙ্গাপুর", "Singapore", "সিঙ্গাপুর", "Singapore", "Asia/Singapore", "🇸🇬", 1.3521, 103.8198, "দক্ষিণ ও পূর্ব এশিয়া"),

        // --- ইউরোপ ও আমেরিকা ---
        WorldLocation("gb_london", "যুক্তরাজ্য", "United Kingdom", "লন্ডন", "London", "Europe/London", "🇬🇧", 51.5074, -0.1278, "ইউরোপ ও আমেরিকা"),
        WorldLocation("us_ny", "যুক্তরাষ্ট্র", "United States", "নিউইয়র্ক", "New York", "America/New_York", "🇺🇸", 40.7128, -74.0060, "ইউরোপ ও আমেরিকা"),
        WorldLocation("ca_toronto", "কানাডা", "Canada", "টরন্টো", "Toronto", "America/Toronto", "🇨🇦", 43.6532, -79.3832, "ইউরোপ ও আমেরিকা")
    )

    private val BENGALI_DIGITS = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
    private val ARABIC_DIGITS = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')

    private val HIJRI_MONTHS_BN = listOf(
        "মুহাররম", "সফর", "রবিউল আউয়াল", "রবিউস সানি",
        "জুমাদাল উলা", "জুমাদাল উখরা", "রজব", "শাবান",
        "রমজান", "শাওয়াল", "জিলকদ", "জিলহজ্ব"
    )

    private val HIJRI_MONTHS_AR = listOf(
        "المحرّم", "صفر", "ربيع الأول", "ربيع الثاني",
        "جمادى الأولى", "جمادى الآخرة", "رجب", "شعبان",
        "رمضان", "شوّال", "ذو القعدة", "ذو الحجة"
    )

    private val HIJRI_MONTHS_EN = listOf(
        "Muharram", "Safar", "Rabi' al-Awwal", "Rabi' al-Thani",
        "Jumada al-Ula", "Jumada al-Ukhra", "Rajab", "Sha'ban",
        "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah"
    )

    private val BENGALI_DAYS = mapOf(
        Calendar.SATURDAY to "শনিবার",
        Calendar.SUNDAY to "রবিবার",
        Calendar.MONDAY to "সোমবার",
        Calendar.TUESDAY to "মঙ্গলবার",
        Calendar.WEDNESDAY to "বুধবার",
        Calendar.THURSDAY to "বৃহস্পতিবার",
        Calendar.FRIDAY to "শুক্রবার"
    )

    private val BENGALI_MONTHS = listOf(
        "জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন",
        "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর"
    )

    fun toBengaliDigits(number: Int): String = toBengaliDigits(number.toString())

    fun toBengaliDigits(str: String): String {
        val sb = StringBuilder()
        for (ch in str) {
            if (ch in '0'..'9') {
                sb.append(BENGALI_DIGITS[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    fun toArabicDigits(number: Int): String = toArabicDigits(number.toString())

    fun toArabicDigits(str: String): String {
        val sb = StringBuilder()
        for (ch in str) {
            if (ch in '0'..'9') {
                sb.append(ARABIC_DIGITS[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    fun getCalendarForTimezone(timezoneId: String): Calendar {
        val tz = try {
            TimeZone.getTimeZone(timezoneId)
        } catch (_: Exception) {
            TimeZone.getTimeZone("Asia/Dhaka")
        }
        return Calendar.getInstance(tz)
    }

    fun getLiveTime(timezoneId: String): LiveTimeResult {
        val cal = getCalendarForTimezone(timezoneId)
        val hour24 = cal.get(Calendar.HOUR_OF_DAY)
        val hour12 = cal.get(Calendar.HOUR).let { if (it == 0) 12 else it }
        val minute = cal.get(Calendar.MINUTE)
        val second = cal.get(Calendar.SECOND)
        val isPm = hour24 >= 12
        val amPm = if (isPm) "PM" else "AM"

        val time12En = String.format(Locale.US, "%02d:%02d:%02d %s", hour12, minute, second, amPm)
        val time12Bn = String.format(Locale.US, "%02d:%02d:%02d %s", hour12, minute, second, amPm)
            .let { toBengaliDigits(it) }

        val tz = cal.timeZone
        val offsetMs = tz.getOffset(cal.timeInMillis)
        val offsetHours = offsetMs / (1000 * 60 * 60)
        val offsetMinutes = kotlin.math.abs((offsetMs / (1000 * 60)) % 60)
        val sign = if (offsetHours >= 0) "+" else "-"
        val offsetStr = String.format(Locale.US, "GMT%s%02d:%02d", sign, kotlin.math.abs(offsetHours), offsetMinutes)

        return LiveTimeResult(
            time12En = time12En,
            time12Bn = time12Bn,
            hour12 = hour12,
            minute = minute,
            second = second,
            isPm = isPm,
            amPmString = amPm,
            timezoneOffsetString = offsetStr
        )
    }

    fun getEnglishDate(timezoneId: String): EnglishDateResult {
        val cal = getCalendarForTimezone(timezoneId)
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
        val monthIdx = cal.get(Calendar.MONTH)
        val year = cal.get(Calendar.YEAR)

        val enSdf = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.US).apply {
            timeZone = cal.timeZone
        }
        val formattedEn = enSdf.format(cal.time)

        val dayNameBn = BENGALI_DAYS[dayOfWeek] ?: "আজ"
        val monthNameBn = BENGALI_MONTHS.getOrElse(monthIdx) { "" }
        val dayBn = toBengaliDigits(dayOfMonth)
        val yearBn = toBengaliDigits(year)
        val formattedBn = "$dayNameBn, $dayBn $monthNameBn $yearBn"

        val dayNameEnSdf = SimpleDateFormat("EEEE", Locale.US).apply { timeZone = cal.timeZone }
        val dayNameEn = dayNameEnSdf.format(cal.time)

        val dateString = String.format(Locale.US, "%04d-%02d-%02d", year, monthIdx + 1, dayOfMonth)

        return EnglishDateResult(
            formattedFullEn = formattedEn,
            formattedFullBn = formattedBn,
            dayNameEn = dayNameEn,
            dayNameBn = dayNameBn,
            dateString = dateString
        )
    }

    fun getHijriDate(timezoneId: String, offsetDays: Int = 0): HijriDateResult {
        val cal = getCalendarForTimezone(timezoneId)
        if (offsetDays != 0) {
            cal.add(Calendar.DAY_OF_MONTH, offsetDays)
        }

        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)

        val (hYear, hMonth, hDay) = gregorianToHijri(year, month, day)

        val dayBn = toBengaliDigits(hDay)
        val dayAr = toArabicDigits(hDay)
        val monthNameBn = HIJRI_MONTHS_BN.getOrElse(hMonth - 1) { "" }
        val monthNameAr = HIJRI_MONTHS_AR.getOrElse(hMonth - 1) { "" }
        val monthNameEn = HIJRI_MONTHS_EN.getOrElse(hMonth - 1) { "" }
        val yearBn = toBengaliDigits(hYear)
        val yearAr = toArabicDigits(hYear)

        val formattedBn = "$dayBn $monthNameBn $yearBn হিজরি"
        val formattedAr = "$dayAr $monthNameAr $yearAr هـ"
        val formattedEn = "$hDay $monthNameEn $hYear AH"

        return HijriDateResult(
            day = hDay,
            month = hMonth,
            year = hYear,
            dayBn = dayBn,
            dayAr = dayAr,
            monthNameBn = monthNameBn,
            monthNameAr = monthNameAr,
            monthNameEn = monthNameEn,
            yearBn = yearBn,
            yearAr = yearAr,
            formattedFullBn = formattedBn,
            formattedFullAr = formattedAr,
            formattedFullEn = formattedEn
        )
    }

    private fun gregorianToHijri(y: Int, m: Int, d: Int): Triple<Int, Int, Int> {
        var a = (14 - m) / 12
        var y1 = y + 4800 - a
        var m1 = m + 12 * a - 3
        var jd = d + ((153 * m1 + 2) / 5) + 365 * y1 + (y1 / 4) - (y1 / 100) + (y1 / 400) - 32045

        var l = jd - 1948440 + 10632
        var n = (l - 1) / 10631
        var l1 = l - 10631 * n + 354
        var j = ((10985 - l1) / 5316) * ((50 * l1) / 17719) + (l1 / 5670) * ((43 * l1) / 15238)
        var l2 = l1 - ((30 - j) / 15) * ((17719 * j) / 50) - (j / 16) * ((15238 * j) / 43) + 29
        var m2 = (24 * l2) / 709
        var d2 = l2 - (709 * m2) / 24
        var y2 = 30 * n + j - 30

        return Triple(y2.coerceAtLeast(1400), m2.coerceIn(1, 12), d2.coerceIn(1, 30))
    }

    fun calculatePrayerTimes(location: WorldLocation): LocationPrayerTimes {
        val cal = getCalendarForTimezone(location.timezoneId)
        val dayOfYear = cal.get(Calendar.DAY_OF_YEAR)
        val lat = location.lat
        val lng = location.lng
        val tzOffsetHours = cal.timeZone.getOffset(cal.timeInMillis) / (1000.0 * 60.0 * 60.0)

        // Solar Declination & Equation of Time calculation
        val b = 2.0 * Math.PI * (dayOfYear - 81) / 365.0
        val eot = 9.87 * sin(2 * b) - 7.53 * cos(b) - 1.5 * sin(b)
        val declination = 23.45 * sin(Math.toRadians((360.0 / 365.0) * (dayOfYear - 81)))

        val solarNoonHours = 12.0 + (tzOffsetHours * 15.0 - lng) / 15.0 - (eot / 60.0)

        fun hourAngle(altitudeDeg: Double): Double {
            val phi = Math.toRadians(lat)
            val delta = Math.toRadians(declination)
            val alpha = Math.toRadians(altitudeDeg)
            val cosH = (sin(alpha) - sin(phi) * sin(delta)) / (cos(phi) * cos(delta))
            return if (cosH < -1.0) 180.0 else if (cosH > 1.0) 0.0 else Math.toDegrees(acos(cosH))
        }

        // Fajr (18 degrees below horizon)
        val fajrHa = hourAngle(-18.0)
        val fajrHours = solarNoonHours - (fajrHa / 15.0)

        // Sunrise (0.833 degrees below horizon for refraction)
        val sunriseHa = hourAngle(-0.833)
        val sunriseHours = solarNoonHours - (sunriseHa / 15.0)

        // Dhuhr (solar noon + 2 min)
        val dhuhrHours = solarNoonHours + (2.0 / 60.0)

        // Asr (Hanafi/Standard calculation)
        val phi = Math.toRadians(lat)
        val delta = Math.toRadians(declination)
        val asrAlt = Math.toDegrees(atan2(1.0, 1.0 + tan(kotlin.math.abs(phi - delta))))
        val asrHa = hourAngle(asrAlt)
        val asrHours = solarNoonHours + (asrHa / 15.0)

        // Maghrib (Sunset)
        val maghribHours = solarNoonHours + (sunriseHa / 15.0) + (2.0 / 60.0)

        // Isha (18 degrees below horizon)
        val ishaHours = solarNoonHours + (fajrHa / 15.0)

        // Tahajjud end / Ishraq start
        val ishraqHours = sunriseHours + (15.0 / 60.0)

        fun formatHours(h: Double): String {
            var norm = h
            while (norm < 0) norm += 24.0
            while (norm >= 24) norm -= 24.0
            val hours = norm.toInt()
            val minutes = ((norm - hours) * 60).toInt()
            val h12 = if (hours % 12 == 0) 12 else hours % 12
            val amPm = if (hours >= 12) "PM" else "AM"
            val raw = String.format(Locale.US, "%02d:%02d %s", h12, minutes, amPm)
            return toBengaliDigits(raw)
        }

        fun toMinutes(h: Double): Int {
            var norm = h
            while (norm < 0) norm += 24.0
            while (norm >= 24) norm -= 24.0
            val hr = norm.toInt()
            val min = ((norm - hr) * 60).toInt()
            return hr * 60 + min
        }

        val nowMinutes = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)

        val fajrM = toMinutes(fajrHours)
        val sunriseM = toMinutes(sunriseHours)
        val dhuhrM = toMinutes(dhuhrHours)
        val asrM = toMinutes(asrHours)
        val maghribM = toMinutes(maghribHours)
        val ishaM = toMinutes(ishaHours)

        // Determine next prayer
        val nextName: String
        val remainingMin: Int
        if (nowMinutes < fajrM) {
            nextName = "ফজর"
            remainingMin = fajrM - nowMinutes
        } else if (nowMinutes < sunriseM) {
            nextName = "সূর্যোদয়"
            remainingMin = sunriseM - nowMinutes
        } else if (nowMinutes < dhuhrM) {
            nextName = "যোহর"
            remainingMin = dhuhrM - nowMinutes
        } else if (nowMinutes < asrM) {
            nextName = "আসর"
            remainingMin = asrM - nowMinutes
        } else if (nowMinutes < maghribM) {
            nextName = "মাগরিব"
            remainingMin = maghribM - nowMinutes
        } else if (nowMinutes < ishaM) {
            nextName = "ইশা"
            remainingMin = ishaM - nowMinutes
        } else {
            nextName = "আগামীকাল ফজর"
            remainingMin = (24 * 60 - nowMinutes) + fajrM
        }

        val remHours = remainingMin / 60
        val remMins = remainingMin % 60
        val remainingStr = if (remHours > 0) {
            "${toBengaliDigits(remHours)} ঘণ্টা ${toBengaliDigits(remMins)} মিনিট বাকি"
        } else {
            "${toBengaliDigits(remMins)} মিনিট বাকি"
        }

        val prayers = listOf(
            PrayerTimeItem(
                id = "fajr",
                nameBn = "ফজর",
                nameEn = "Fajr",
                nameAr = "الفجر",
                timeFormatted = formatHours(fajrHours),
                endTimeFormatted = formatHours(sunriseHours),
                rakatsDescription = "২ রাকাত সুন্নত + ২ রাকাত ফরজ = মোট ৪ রাকাত",
                hadithVirtue = "যে ব্যক্তি ফজরের নামাজ জামাতে আদায় করে, সে যেন সারারাত দাঁড়িয়ে নামাজ পড়ল। (সহীহ মুসলিম)",
                isCurrent = nowMinutes in fajrM until sunriseM,
                isNext = nextName == "ফজর"
            ),
            PrayerTimeItem(
                id = "sunrise",
                nameBn = "সূর্যোদয়",
                nameEn = "Sunrise",
                nameAr = "الشروق",
                timeFormatted = formatHours(sunriseHours),
                endTimeFormatted = formatHours(ishraqHours),
                rakatsDescription = "সূর্যোদয়ের সময় নামাজ নিষিদ্ধ; সূর্যোদয়ের ১৫-২০ মিনিট পর ২/৪ রাকাত ইশরাক",
                hadithVirtue = "যে ব্যক্তি ফজরের পর সূর্যোদয় পর্যন্ত জিকির করে দুই রাকাত নামাজ পড়ে, সে একটি পূর্ণ হজ্জ ও ওমরার সওয়াব পায়। (তিরমিযী)",
                isCurrent = false,
                isNext = nextName == "সূর্যোদয়"
            ),
            PrayerTimeItem(
                id = "dhuhr",
                nameBn = "যোহর",
                nameEn = "Dhuhr",
                nameAr = "الظهر",
                timeFormatted = formatHours(dhuhrHours),
                endTimeFormatted = formatHours(asrHours),
                rakatsDescription = "৪ রাকাত সুন্নত + ৪ রাকাত ফরজ + ২ রাকাত সুন্নত + ২ রাকাত নফল = মোট ১২ রাকাত",
                hadithVirtue = "সূর্য ঢলে পড়ার সময় আসমানের দরজা খুলে দেওয়া হয়, আমি ভালোবাসি তখন আমার নেক আমল উপরে উঠুক। (তিরমিযী)",
                isCurrent = nowMinutes in dhuhrM until asrM,
                isNext = nextName == "যোহর"
            ),
            PrayerTimeItem(
                id = "asr",
                nameBn = "আসর",
                nameEn = "Asr",
                nameAr = "العصر",
                timeFormatted = formatHours(asrHours),
                endTimeFormatted = formatHours(maghribHours),
                rakatsDescription = "৪ রাকাত সুন্নত (গায়রে মুয়াক্কাদাহ) + ৪ রাকাত ফরজ = মোট ৮ রাকাত",
                hadithVirtue = "যে ব্যক্তি দুই শীতল সময়ের (ফজর ও আসর) নামাজ পড়বে, সে জান্নাতে প্রবেশ করবে। (সহীহ বুখারী)",
                isCurrent = nowMinutes in asrM until maghribM,
                isNext = nextName == "আসর"
            ),
            PrayerTimeItem(
                id = "maghrib",
                nameBn = "মাগরিব",
                nameEn = "Maghrib",
                nameAr = "المغرب",
                timeFormatted = formatHours(maghribHours),
                endTimeFormatted = formatHours(ishaHours),
                rakatsDescription = "৩ রাকাত ফরজ + ২ রাকাত সুন্নত + ২ রাকাত নফল (আউয়াবীন) = মোট ৭ রাকাত",
                hadithVirtue = "যে ব্যক্তি মাগরিবের পর ৬ রাকাত নফল নামাজ পড়বে এবং মন্দ কথা বলবে না, তার ১২ বছরের ইবাদতের সওয়াব হবে। (তিরমিযী)",
                isCurrent = nowMinutes in maghribM until ishaM,
                isNext = nextName == "মাগরিব"
            ),
            PrayerTimeItem(
                id = "isha",
                nameBn = "ইশা",
                nameEn = "Isha",
                nameAr = "العشاء",
                timeFormatted = formatHours(ishaHours),
                endTimeFormatted = formatHours(fajrHours),
                rakatsDescription = "৪ রাকাত সুন্নত + ৪ রাকাত ফরজ + ২ রাকাত সুন্নত + ২ রাকাত নফল + ৩ রাকাত বিতর = মোট ১৫ রাকাত",
                hadithVirtue = "যে ব্যক্তি ইশার নামাজ জামাতে আদায় করে, সে যেন অর্ধেক রাত নফল নামাজ পড়ল। (সহীহ মুসলিম)",
                isCurrent = nowMinutes >= ishaM || nowMinutes < fajrM,
                isNext = nextName.contains("ইশা")
            )
        )

        // 3 Prohibited / Makruh times
        val forbidden = listOf(
            ForbiddenTimeItem(
                titleBn = "১. সূর্যোদয়ের সময়",
                periodFormatted = "${formatHours(sunriseHours)} থেকে ${formatHours(ishraqHours)}",
                descriptionBn = "সূর্য উদিত হওয়ার সময় থেকে আনুমানিক ১৫-২০ মিনিট পর পর্যন্ত যেকোনো নামাজ পড়া সম্পূর্ণ নিষিদ্ধ।"
            ),
            ForbiddenTimeItem(
                titleBn = "২. ঠিক দ্বিপ্রহরের সময়",
                periodFormatted = "${formatHours(solarNoonHours - (10.0 / 60.0))} থেকে ${formatHours(dhuhrHours)}",
                descriptionBn = "সূর্য ঠিক মাথার উপর থাকাকালে (যোহরের ওয়াক্ত শুরু হওয়ার পূর্ববর্তী ১০-১৫ মিনিট) নামাজ পড়া নিষিদ্ধ।"
            ),
            ForbiddenTimeItem(
                titleBn = "৩. ঠিক সূর্যাস্তের সময়",
                periodFormatted = "${formatHours(maghribHours - (15.0 / 60.0))} থেকে ${formatHours(maghribHours)}",
                descriptionBn = "সূর্য হলুদ ও লালবর্ণ ধারণ করে অস্ত যাওয়ার মুহূর্ত পর্যন্ত সব ধরনের নামাজ পড়া নিষিদ্ধ (তবে ঐ দিনের আসর বাকি থাকলে পড়া যাবে)।"
            )
        )

        val nextPrayerTime = when (nextName) {
            "ফজর", "আগামীকাল ফজর" -> formatHours(fajrHours)
            "সূর্যোদয়" -> formatHours(sunriseHours)
            "যোহর" -> formatHours(dhuhrHours)
            "আসর" -> formatHours(asrHours)
            "মাগরিব" -> formatHours(maghribHours)
            "ইশা" -> formatHours(ishaHours)
            else -> ""
        }
        val remainingCompact = if (remHours > 0) {
            "${toBengaliDigits(remHours)} ঘণ্টা ${toBengaliDigits(remMins)} মি."
        } else {
            "${toBengaliDigits(remMins)} মি."
        }

        return LocationPrayerTimes(
            fajr = formatHours(fajrHours),
            sunrise = formatHours(sunriseHours),
            dhuhr = formatHours(dhuhrHours),
            asr = formatHours(asrHours),
            maghrib = formatHours(maghribHours),
            isha = formatHours(ishaHours),
            tahajjudEnd = formatHours(fajrHours - (10.0 / 60.0)),
            ishraqStart = formatHours(ishraqHours),
            nextPrayerNameBn = nextName,
            nextPrayerRemaining = remainingStr,
            nextPrayerTimeFormatted = nextPrayerTime,
            nextPrayerRemainingCompact = remainingCompact,
            allPrayers = prayers,
            forbiddenTimes = forbidden
        )
    }

    fun searchLocations(query: String): List<WorldLocation> {
        val q = query.trim().lowercase(Locale.ROOT)
        if (q.isEmpty()) return POPULAR_LOCATIONS

        val filteredPopular = POPULAR_LOCATIONS.filter { loc ->
            loc.cityBn.lowercase(Locale.ROOT).contains(q) ||
            loc.cityEn.lowercase(Locale.ROOT).contains(q) ||
            loc.countryBn.lowercase(Locale.ROOT).contains(q) ||
            loc.countryEn.lowercase(Locale.ROOT).contains(q) ||
            loc.region.lowercase(Locale.ROOT).contains(q) ||
            loc.timezoneId.lowercase(Locale.ROOT).contains(q)
        }

        if (filteredPopular.isNotEmpty()) return filteredPopular

        val tzMatches = TimeZone.getAvailableIDs().filter { tzId ->
            tzId.lowercase(Locale.ROOT).contains(q)
        }.take(15).map { tzId ->
            val cleanCity = tzId.substringAfterLast("/").replace("_", " ")
            val cleanCountry = tzId.substringBefore("/", "")
            WorldLocation(
                id = "custom_$tzId",
                countryBn = cleanCountry,
                countryEn = cleanCountry,
                cityBn = cleanCity,
                cityEn = cleanCity,
                timezoneId = tzId,
                flagEmoji = "🌍",
                lat = 20.0,
                lng = 0.0,
                region = "বিশ্ব সময় (World Timezones)"
            )
        }

        return tzMatches.ifEmpty { POPULAR_LOCATIONS }
    }
}
