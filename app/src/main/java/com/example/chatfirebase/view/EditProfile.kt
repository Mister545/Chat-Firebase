package com.example.chatfirebase.view

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.chatfirebase.R
import com.example.chatfirebase.databinding.ActivityEditProfileBinding
import com.example.chatfirebase.viewModel.EditProfileViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.util.Calendar

class EditProfile : AppCompatActivity() {

    private val viewModel: EditProfileViewModel by viewModels()
    lateinit var binding: ActivityEditProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbarInit()
        bottomSheetInit()

        initData()

        calendarInit {
            viewModel.dataOfBirth.value = it
            binding.tDate.text = it
        }
    }

    private fun initData() {
        viewModel.user.observe(this){
            binding.tName.setText(it.name)
            binding.tSecondName.setText(it.name)
            binding.tAboutYou.setText(it.introduceYourSelf)
            binding.tDate.text = if (it.dateOfBerth == "0.0.0000") "--.--.----" else it.dateOfBerth
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu_edit_profile, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit -> {
                viewModel.changeUserData(
                    binding.tName.text.toString(),
                    binding.tSecondName.text.toString(),
                    binding.tAboutYou.text.toString()
                ){
                    if (it)
                        finish()
                    else
                        Log.d("ooo", "error load data to bd")
                }
                true
            }

            R.id.action_settings -> {
                Toast.makeText(this, "Налаштування натиснуто", Toast.LENGTH_SHORT).show()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun toolbarInit() {
        val toolbar: Toolbar = findViewById(R.id.toolbarEditProfile)

        setSupportActionBar(toolbar)

        window.statusBarColor = ContextCompat.getColor(this, R.color.p_ed_toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun calendarInit(callback: (String) -> Unit) {
        val bSaveDataBirth = findViewById<Button>(R.id.save_data_birth)

        val dayPicker: NumberPicker = findViewById(R.id.dayPicker)
        val monthPicker: NumberPicker = findViewById(R.id.monthPicker)
        val yearPicker: NumberPicker = findViewById(R.id.yearPicker)


        fun setDataInCalendar() {
            dayPicker.minValue = 1
            dayPicker.maxValue = 31

            monthPicker.minValue = 1
            monthPicker.displayedValues = arrayOf(
                "JAN", "FEB", "MAR", "APR", "MAY", "JUN",
                "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"
            )
            monthPicker.maxValue = 12

            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            yearPicker.minValue = 1920
            yearPicker.maxValue = currentYear
        }

        setDataInCalendar()

        fun getDaysInMonth(month: Int, year: Int): Int {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month - 1) // Місяці в Calendar починаються з 0
            return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        }

        fun updateDaysInMonth() {
            val selectedMonth = monthPicker.value
            val selectedYear = yearPicker.value
            val daysInMonth = getDaysInMonth(selectedMonth, selectedYear)
            dayPicker.maxValue = daysInMonth
        }

        monthPicker.setOnValueChangedListener { _, _, _ ->
            updateDaysInMonth()
        }

        yearPicker.setOnValueChangedListener { _, _, _ ->
            updateDaysInMonth()
        }

        updateDaysInMonth()


        bSaveDataBirth.setOnClickListener {
            val selectedDay = dayPicker.value
            val selectedMonth =
                monthPicker.displayedValues[monthPicker.value - 1]
            val selectedYear = yearPicker.value

            Toast.makeText(
                this,
                "Дата народження: ${selectedDay}/${selectedMonth}/${selectedYear}",
                Toast.LENGTH_SHORT
            ).show()
            callback("${selectedDay}/${selectedMonth}/${selectedYear}")
        }
    }


    private fun bottomSheetInit() {
        val dimmerView = binding.dimmerView
        val bottomSheetBehavior: BottomSheetBehavior<*>
        val expand = binding.tDate
        val bottomSheet: View = findViewById(R.id.sheet)

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        expand.setOnClickListener {
            dimmerView.visibility = View.VISIBLE
            dimmerView.animate()
                .alpha(1.0f)
                .setDuration(300)
                .start()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        // Показуємо затемнення при відкритті

                    }

                    BottomSheetBehavior.STATE_HIDDEN, BottomSheetBehavior.STATE_COLLAPSED -> {
                        // Ховаємо затемнення при закритті
                        dimmerView.animate()
                            .alpha(0f) // Повернути повну прозорість
                            .setDuration(300)
                            .withEndAction {
                                dimmerView.visibility = View.GONE
                            } // Сховати після завершення
                            .start()
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Плавна анімація затемнення
                dimmerView.alpha = slideOffset
            }
        })

        // Закриття BottomSheet при натисканні на фон
        dimmerView.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }


    private fun getMonthFormat(month: Int): String {

        return when (month) {
            1 -> "JAN"
            2 -> "FEB"
            3 -> "MAR"
            4 -> "APR"
            5 -> "MAY"
            6 -> "JUN"
            7 -> "JUL"
            8 -> "AUG"
            9 -> "SEP"
            10 -> "OCT"
            11 -> "NOV"
            12 -> "DEC"
            else -> "JAN"
        }
    }
}