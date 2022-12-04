package com.knocklock.presentation.ui.setting.credit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.knocklock.presentation.R

@Composable
fun DeveloperList(
    modifier: Modifier = Modifier,
    devList: List<Developer>
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(devList) { dev ->
            DeveloperItem(developer = dev)
        }
    }
}


@Preview
@Composable
private fun PreviewDeveloperList() {
    val tmpDev = mutableListOf<Developer>().apply {
        add(Developer("Daq", painterResource(id = R.drawable.hyunkuk), "안녕하슈"))
        add(Developer("Jaeryo", painterResource(id = R.drawable.minuk), "안녕하슈"))
        add(Developer("Hence", painterResource(id = R.drawable.hyunsu), "안녕하슈"))
    }
    DeveloperList(devList = tmpDev)
}