package com.example.calculator
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.calculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private var inputValue1:Double?=0.0
    private var inputValue2:Double?=null
    private var currentOperator: Operator?=null
    private var result:Double?=null
    private val equation:StringBuilder=StringBuilder().append(ZERO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()
        enableEdgeToEdge()
        setContentView(binding.root)
        setNightMode()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun setListeners(){
        for(button in getNumericButtons()){
            button.setOnClickListener{onNumberClicked(button.text.toString())}
        }
        with(binding){
            buttonZero.setOnClickListener { (onZeroClicked()) }
            buttonDoubleZero.setOnClickListener { (onDoubleZeroClicked()) }
            buttonDecimalPoint.setOnClickListener{(onDecimalPointClicked())}
            buttonAddition.setOnClickListener{(onOperatorClicked(Operator.ADDITION))}
            buttonSubtraction.setOnClickListener{(onOperatorClicked(Operator.SUBTRACTION))}
            buttonMultiplication.setOnClickListener{(onOperatorClicked(Operator.MULTIPLICATION))}
            buttonDivision.setOnClickListener{(onOperatorClicked(Operator.DIVISION))}
            buttonEquals.setOnClickListener{(onEqualClicked())}
            buttonAllClear.setOnClickListener{(onALlClearClicked())}
            buttonDelete.setOnClickListener({(onDeleteClicked())})
            imageNightMode.setOnClickListener{toggleNightMode()}
        }
    }
    private fun toggleNightMode(){
        if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES){
            AppCompatDelegate.setDefaultNightMode((AppCompatDelegate.MODE_NIGHT_NO))
        }
        else{
            AppCompatDelegate.setDefaultNightMode((AppCompatDelegate.MODE_NIGHT_YES))
        }
        recreate()
    }
    private fun setNightMode(){
        if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES){
            binding.imageNightMode.setImageResource(R.drawable.ic_sun)
        }
        else{
            binding.imageNightMode.setImageResource((R.drawable.ic_moon))
        }
    }
    private fun onDeleteClicked(){
        if (equation.isNotEmpty() && equation.toString() != ZERO) {
            equation.deleteCharAt(equation.length - 1)
            if (equation.isEmpty()) {
                equation.append(ZERO)
            }
            setInput()
            updateInputOnDisplay()
        }
    }
    private fun onALlClearClicked(){
        inputValue1=0.0
        inputValue2=null
        currentOperator=null
        result=null
        equation.clear().append(ZERO)
        clearDisplay()
    }
    private fun onOperatorClicked(operator: Operator){
        onEqualClicked()
        currentOperator=operator
    }
    private fun onEqualClicked(){
        if(inputValue2!=null){
            result=calculate()
            equation.clear().append(ZERO)
            updateResultOnDisplay()
            inputValue1=result
            result=null
            inputValue2=null
            currentOperator=null
        }
        else{
            equation.clear().append(ZERO)
        }
    }
    private fun calculate():Double{
        return when(requireNotNull(currentOperator)){
            Operator.ADDITION->getInputValue1()+getInputValue2()
            Operator.SUBTRACTION->getInputValue1()-getInputValue2()
            Operator.MULTIPLICATION->getInputValue1()*getInputValue2()
            Operator.DIVISION->getInputValue1()/getInputValue2()
        }
    }
    private fun onDecimalPointClicked(){
        if(equation.contains(DECIMAL_POINT)) return
        equation.append((DECIMAL_POINT))
        setInput()
        updateInputOnDisplay()
    }
    private fun onZeroClicked(){
        if(equation.startsWith(ZERO)) return
        onNumberClicked(ZERO)
    }private fun onDoubleZeroClicked(){
        if(equation.startsWith(ZERO)) return
        onNumberClicked(DOUBLE_ZERO)
    }
    private fun getNumericButtons()=with(binding){
        arrayOf(
            buttonOne,
            buttonTwo,
            buttonThree,
            buttonFour,
            buttonFive,
            buttonSix,
            buttonSeven,
            buttonEight,
            buttonNine
        )
    }
    private fun updateResultOnDisplay(isPercentage:Boolean=false){
        binding.textInput.text=getFormattedDisplayValue(value=result)
        var input2Text=getFormattedDisplayValue(value=getInputValue2())
        if(isPercentage)input2Text="$input2Text${getString(R.string.percentage)}"
        binding.textEquation.text=String.format(
            "%s %s %s",
            getFormattedDisplayValue(value=getInputValue1()),
            getOperatorSymbol(),
            input2Text
        )
    }
    private fun onNumberClicked(numberText:String){
        if(equation.startsWith(ZERO)){
            equation.deleteCharAt(0)
        }
        else if(equation.startsWith("$MINUS$ZERO")){
            equation.deleteCharAt(1)
        }
        equation.append(numberText)
        setInput()
        updateInputOnDisplay()
    }
    private fun setInput(){
        if(currentOperator==null){
            inputValue1=equation.toString().toDouble()
        }
        else{
            inputValue2=equation.toString().toDouble()
        }
    }
    private fun clearDisplay(){
        with(binding){
            textInput.text=getFormattedDisplayValue(value=getInputValue1())
            textEquation.text=null
        }
    }
    private fun updateInputOnDisplay(){
        if(result==null){
            binding.textEquation.text=null
        }
        binding.textInput.text=equation
    }
    private fun getInputValue1()=inputValue1?:0.0
    private fun getInputValue2()=inputValue2?:0.0
    private fun getOperatorSymbol():String{
        return when(requireNotNull(currentOperator)){
            Operator.ADDITION->getString(R.string.addition)
            Operator.SUBTRACTION->getString(R.string.subtraction)
            Operator.MULTIPLICATION->getString(R.string.multiplication)
            Operator.DIVISION->getString(R.string.division)
        }
    }
    private fun getFormattedDisplayValue(value:Double?):String?{
        val originalValue=value?:return null
        return if (originalValue%1==0.0){
            originalValue.toInt().toString()
        }
        else{
            originalValue.toString()
        }
    }
    enum class Operator{
        ADDITION,SUBTRACTION,MULTIPLICATION,DIVISION
    }
    private companion object {
        const val DECIMAL_POINT="."
        const val ZERO="0"
        const val DOUBLE_ZERO="00"
        const val MINUS="-"
    }
}