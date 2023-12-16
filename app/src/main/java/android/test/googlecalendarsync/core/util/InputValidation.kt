package android.test.googlecalendarsync.core.util

import java.util.regex.Pattern

class InputValidation private constructor(
    private val emptyValidation: Boolean,
    private val minLengthValidation: Int?,
    private val patternsValidation: List<Pattern>,
    private val lambdasValidation: MutableList<() -> Boolean>,
) {
    fun validate(input: String?): InputValidationType {
        if (emptyValidation) {
            if (fieldIsEmpty(input)) return InputValidationType.FieldIsEmpty
        }
        if (minLengthValidation != -1) {
            if (fieldIsLessThanMinLength(input, minLengthValidation)) return InputValidationType.FieldLessThanMinLength
        }
        patternsValidation.forEach {
            if (!fieldIsMatch(input, it)) return InputValidationType.FieldIsNotMatchPatterns
        }
        lambdasValidation.forEach {
            if(!it()) return InputValidationType.FieldIsNotMatchLambda
        }
        return InputValidationType.FieldIsValid
    }

    fun isValid(input: String?, onValidated: (Boolean) -> Unit): Boolean {
        val isFieldIsValid = validate(input) == InputValidationType.FieldIsValid
        onValidated(isFieldIsValid)
        return isFieldIsValid
    }

    fun validate(input: String?, onNotValid: (InputValidationType) -> Unit = {}, onValid: () -> Unit = {}): InputValidationType {
        val result = validate(input)
        if (result == InputValidationType.FieldIsValid) onValid()
        else onNotValid(result)
        return result
    }

    private fun fieldIsMatch(input: String?, pattern: Pattern): Boolean {
        if (input.isNullOrEmpty()) return true
        return pattern.matcher(input).matches()
    }

    private fun fieldIsEmpty(input: String?): Boolean {
        return input?.trim().isNullOrBlank()
    }

    private fun fieldIsLessThanMinLength(input: String?, minLength: Int?): Boolean {
        minLength ?: return false
        if (fieldIsEmpty(input)) return false
        return input!!.length < minLength
    }

    class Builder {
        var emptyValidation: Boolean = false
            private set
        var minLengthValidation: Int? = null
            private set
        var patternsValidation: MutableList<Pattern> = mutableListOf()
            private set
        var lambdasValidation: MutableList<() -> Boolean> = mutableListOf()
            private set

        fun validateEmpty(): Builder = apply {
            this.emptyValidation = true
        }

        fun validatePatterns(pattern: Pattern): Builder = apply {
            patternsValidation.add(pattern)
        }

        fun validateLambda(block: () -> Boolean) {
            lambdasValidation.add(block)
        }

        fun validateMinLength(minLength: Int): Builder = apply {
            this.minLengthValidation = minLength
        }

        fun build(): InputValidation {
            return InputValidation(emptyValidation, minLengthValidation, patternsValidation, lambdasValidation)
        }
    }
}

enum class InputValidationType {
    FieldIsEmpty,
    FieldLessThanMinLength,
    FieldIsNotMatchPatterns,
    FieldIsNotMatchLambda,
    FieldIsValid,
}