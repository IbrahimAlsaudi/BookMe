package com.example.bookme.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bookme.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DateSelector(
    checkIn: LocalDate?,
    checkOut: LocalDate?,
    onClick: () -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DateColumn(stringResource(R.string.check_in_label), checkIn?.format(formatter) ?: stringResource(R.string.date_select_placeholder))
            VerticalDivider(modifier = Modifier.height(32.dp))
            DateColumn(stringResource(R.string.check_out_label), checkOut?.format(formatter) ?: stringResource(R.string.date_select_placeholder))
        }
    }
}

@Composable
private fun DateColumn(label: String, date: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(text = date, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
    }
}
