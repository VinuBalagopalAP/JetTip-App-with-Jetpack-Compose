package com.example.jittipapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.jittipapp.components.InputField
import com.example.jittipapp.ui.theme.JitTipAppTheme
import com.example.jittipapp.utils.calculateTotalTip
import com.example.jittipapp.utils.calulateTotalPerPerson
import com.example.jittipapp.widgets.RoundIconButton


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JitTipAppTheme {
                MyApp {
                    Column {
                        MainContent()
                    }
                }
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    // A surface container using the 'background' color from the theme
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        content()
    }
}

@Preview
@Composable
fun MainContent(){
    val splitByState = remember {
        mutableStateOf(1)
    }

    val range = IntRange(start = 1, endInclusive = 100)

    val tipAmountState = remember {
        mutableStateOf(0.0)
    }

    val totalPerPerson = remember {
        mutableStateOf(0.0)
    }

    BillForm(
        range = range,
        splitByState =splitByState,
        tipAmountState = tipAmountState,
        totalPerPerson = totalPerPerson,
    ){
            billAmt -> Log.d("AMT", "MainContent: $billAmt")
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    range: IntRange = 1..100,
    splitByState: MutableState<Int>,
    tipAmountState: MutableState<Double>,
    totalPerPerson: MutableState<Double>,
    onValChange: (String) -> Unit = {}
){
    val totalBillState = remember {
        mutableStateOf("")
    }

    val validState = remember(totalBillState.value){
        totalBillState.value.trim().isNotEmpty()
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    val sliderPositionState = remember {
        mutableStateOf(0f)
    }

    val tipPercentage = (sliderPositionState.value * 100).toInt()


    TopHeader(totalPerPerson = totalPerPerson.value)

    Surface(
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth()
            .clip(shape = CircleShape.copy(all = CornerSize(8.dp))),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)) {
        Column(
            modifier = modifier.padding(6.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,) {
            InputField(
                valueState = totalBillState,
                labelId = "Enter Bill",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if (!validState) return@KeyboardActions
                    onValChange(totalBillState.value.trim())

                    keyboardController?.hide()
                })

                if(validState){
                    Row(
                        modifier=modifier.padding(3.dp),
                        horizontalArrangement = Arrangement.Start,)
                    {
                        Text(
                            text = "Split",
                            modifier=modifier.align(
                                alignment = Alignment.CenterVertically
                            ))
                        Spacer(modifier = modifier.width(120.dp))
                        Row(
                            modifier=modifier.padding(3.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically){
                            RoundIconButton(
                                imageVector = Icons.Rounded.Remove,
                                onClick = {
                                    Log.d("Icon", "BillForm: Removed")

                                    splitByState.value =
                                        if (splitByState.value > 1) splitByState.value - 1
                                        else
                                            1

                                    totalPerPerson.value =
                                        calulateTotalPerPerson(
                                            totalBillState.value.toDouble(),
                                            splitByState.value,
                                            tipPercentage
                                        )
                                })

                            Text(
                                text = "${splitByState.value}",
                                modifier= modifier
                                    .align(
                                        alignment = Alignment.CenterVertically
                                    )
                                    .padding(start = 9.dp, end = 9.dp))

                            RoundIconButton(
                                imageVector = Icons.Rounded.Add,
                                onClick = {
                                    Log.d("Icon", "BillForm: Added")

                                    if (splitByState.value < range.last) {
                                        splitByState.value = splitByState.value + 1
                                    }

                                    totalPerPerson.value =
                                        calulateTotalPerPerson(
                                            totalBillState.value.toDouble(),
                                            splitByState.value,
                                            tipPercentage
                                        )
                                })
                        }}

                    Row(modifier= modifier
                        .padding(
                            horizontal = 3.dp,
                            vertical = 12.dp)) {
                        Text(text = "Tip",
                            modifier= modifier.align(alignment = Alignment.CenterVertically))

                        Spacer(modifier = modifier.width(200.dp))

                        Text(text = "$ ${tipAmountState.value}",
                            modifier= Modifier.align(alignment = Alignment.CenterVertically)) }
                    Column(
                        modifier = modifier
                            .padding(
                                horizontal = 3.dp,
                                vertical = 12.dp
                            ),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally

                    ) {
                        Text(text = "$tipPercentage %")

                        Spacer(modifier = modifier.height(14.dp))

                        Slider(
                            value = sliderPositionState.value,
                            onValueChange = { newVal->
                                sliderPositionState.value = newVal

                                tipAmountState.value =
                                    calculateTotalTip(totalBill = totalBillState.value.toDouble(),tipPercentage = tipPercentage)

                                totalPerPerson.value =
                                    calulateTotalPerPerson(
                                        totalBillState.value.toDouble(),
                                        splitByState.value,
                                        tipPercentage
                                    )
                                Log.d("Slider", "BillForm: $newVal")},
                            modifier = modifier.padding(
                                start = 16.dp,
                                end= 16.dp),
                            steps = 5,
                            onValueChangeFinished = {
                                Log.d("Slider", "BillForm: Finished")
                            })
                    }
                }
        }}
}


@Preview
@Composable
fun TopHeader(totalPerPerson: Double = 134.0){
    Surface(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(150.dp)
            .clip(
                shape = CircleShape.copy(all = CornerSize(12.dp))
            ),
        color = Color(0xFFE9D7F7)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val total = "%.2f".format(totalPerPerson)
            Text(
                text = "Total per person",
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 16.sp
            )
            Text(
                text = "$$total",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 36.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JitTipAppTheme {
        MyApp {
            Text(text = "Hello Again")
        }
    }
}