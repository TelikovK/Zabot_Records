@file:OptIn(ExperimentalMaterialApi::class)

package ru.vb.practice.presentation

import androidx.compose.animation.core.RepeatMode
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffoldDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.domain.models.Client
import com.example.domain.models.Record
import com.example.domain.models.ResultDay
import com.example.domain.models.Visit
import com.facebook.shimmer.Shimmer
import kotlinx.coroutines.delay
import ru.vb.practice.R
import ru.vb.practice.presentation.intent.RecordIntent
import ru.vb.practice.presentation.state.ModalUiState
import ru.vb.practice.presentation.state.RecordUiState
import ru.vb.practice.presentation.state.ResultDayUiState
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.min


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController, mainViewModel: MainViewModel) {
    val uiState by mainViewModel.uiState.collectAsState()
    val modalUiState by mainViewModel.modalUiState.collectAsState()

    Scaffold(
        topBar = { TopAppBarContent() }
        ) { innerPadding ->
        Column(
            modifier = Modifier
                .background(Color(246, 245, 246))
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                    .background(Color.White)
                    .weight(1f)
                    .padding(16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                when (uiState) {
                    is RecordUiState.Loading -> {
                        LoadingScreen(navController)
                    }

                    is RecordUiState.NoRecords -> {
                        NoRecordsScreen(navController)
                    }

                    is RecordUiState.HasRecords -> {
                        RecordsScreen(
                            records = (uiState as RecordUiState.HasRecords).records,
                            mainViewModel
                        )
                        showModalBottom(
                            modalUiState = modalUiState,
                            mainViewModel
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun showModalBottom(modalUiState: ModalUiState, viewModel: MainViewModel) {
    val showModal by viewModel.showModal.collectAsState()
    val SheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(showModal) }

    LaunchedEffect(showModal) {
        showBottomSheet = showModal
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            sheetState = SheetState,
            onDismissRequest = {
                showBottomSheet = false
                viewModel.processIntent(RecordIntent.hideModal)
            },
            dragHandle = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BottomSheetDefaults.DragHandle()
                    Text(text = "Информация о клиенте", fontSize = 16.sp, color = Color(0xFF454558), fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White,
            modifier = Modifier.heightIn(max = 820.dp)
        ) {
            when (modalUiState) {
                is ModalUiState.Loading -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            VisitCardLoad(true)
                        }
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(text = "История визитов клиента",
                                    fontSize = 16.sp,
                                    color = Color(0xFF454558),
                                    fontWeight = FontWeight.Bold)
                            }
                        }
                        items(3)
                        {
                            VisitCardLoad(false)
                        }
                    }

                }

                is ModalUiState.HasClient -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            VisitCard(modalUiState.client, modalUiState.client.currentVisit, true)
                        }
                        if (modalUiState.historyVisits.isNotEmpty()) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.Center
                                ) {
                                Text(
                                    text = "История визитов клиента",
                                    fontSize = 16.sp,
                                    color = Color(0xFF454558),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                            items(modalUiState.historyVisits) { visit ->
                                VisitCard(modalUiState.client, visit, false)
                            }
                        }
                    }
                }
                else -> {
                }
            }
        }
    }
}

@Composable
fun VisitCard(client: Client, visit: Visit, showNamePhone: Boolean) {
    var isLoading by remember {
        mutableStateOf(true)
    }
    LaunchedEffect(key1 = true) {
        delay(5000)
        isLoading = false
    }
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row( modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top, // Выравнивание по верху
                horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    if (showNamePhone == true) {
                        Text(text = "Клиент", fontSize = 14.sp, color = Color(0xFF8097B1))
                        Text(text = "${client.name}")
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Text(text = "Визит от", fontSize = 14.sp, color = Color(0xFF8097B1))
                    Text(text = "${visit.date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Услуги", fontSize = 14.sp, color = Color(0xFF8097B1))
                    Text(text = "${visit.service}")
                }
                Column(modifier = Modifier.weight(1f)) {
                    if (showNamePhone == true) {
                        Text(text = "Номер телефона",fontSize = 14.sp, color = Color(0xFF8097B1))
                        Text(text = "${client.phoneNumber}")
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Text(text = "Мастер", fontSize = 14.sp, color = Color(0xFF8097B1))
                    Text(text = "${visit.master}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Оплата", fontSize = 14.sp, color = Color(0xFF8097B1))
                    Text(text = "${visit.payment}")
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            //TODO комментарий
            Text(text = "Комментарий к клиенту", fontSize = 14.sp, color = Color(0xFF8097B1))
            Text(text = "")
        }
    }
}

@Composable
fun VisitCardLoad(showNamePhone: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    if (showNamePhone == true) {
                        Text(text = "Клиент", fontSize = 14.sp, color = Color(0xFF8097B1))
                        ShimmerListItem(isLoading = true)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Визит от", fontSize = 14.sp, color = Color(0xFF8097B1))
                    ShimmerListItem(isLoading = true)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Услуги", fontSize = 14.sp, color = Color(0xFF8097B1))
                    ShimmerListItem(isLoading = true)
                }
                Column(modifier = Modifier.weight(1f)) {
                    if (showNamePhone == true) {
                        Text(text = "Номер телефона", fontSize = 14.sp, color = Color(0xFF8097B1))
                        ShimmerListItem(isLoading = true)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Мастер", fontSize = 14.sp, color = Color(0xFF8097B1))
                    ShimmerListItem(isLoading = true)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Оплата",fontSize = 14.sp, color = Color(0xFF8097B1))
                    ShimmerListItem(isLoading = true)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Комментарий к клиенту", fontSize = 14.sp, color = Color(0xFF8097B1))
            ShimmerListItem(isLoading = true, modifier = Modifier.fillMaxWidth())

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarContent() {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.icon),
                    contentDescription = "Icon",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Салон",
                        fontSize = 16.sp,
                        color = Color(0xFF454558),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "TOPGUN Санкт-Петербург ТРК Лето",
                        fontSize = 17.sp,
                        color = Color(0xFF454558),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(246, 245, 246),
            titleContentColor = Color.Black
        )
    )
}

@Composable
fun LoadingScreen(navController: NavHostController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxSize()
    ) {

        Text(
            text = "Записи",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF454558)
        )
        Spacer(modifier = Modifier.height(160.dp))
        CircularProgressIndicator(color = Color(0xFF6200EE), strokeWidth = 4.dp)
        Spacer(modifier = Modifier.height(16.dp))

    }
}


@Composable
fun NoRecordsScreen(navController: NavHostController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Записи",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF454558)
        )
        Spacer(modifier = Modifier.height(60.dp))
        Icon(
            painter = painterResource(id = R.drawable.test),
            contentDescription = "Location Iconn",
            tint = Color.Unspecified,
            modifier = Modifier.size(width = 242.dp, height = 186.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Сейчас у вас нет записей",
            fontSize = 16.sp,
            color = Color(0xFF8097B1)

        )
    }
}

@Composable
fun RecordsScreen(
    records: List<Record>,
    viewModel: MainViewModel
) {
        val resultDay by viewModel.resultDay.collectAsState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxSize()
    ) {
        // Текст заголовка
        Text(
            text = "Записи",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF454558)
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
           when(resultDay){
               is ResultDayUiState.HasResult -> {
                   item {
                           resultDayCard((resultDay as ResultDayUiState.HasResult).reuslt)
                   }
               }
               else -> {}
            }
            items(records) { record ->
                RecordCard(record,viewModel)
            }
        }
    }
}

@Composable
fun resultDayCard(resultDay: ResultDay) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                //if(!record.isAccepted) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color(0xFFA672F1), shape = CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
              //  }

                Text(
                    text = "Итоги за сегодняшний день",
                    color = Color(0xFF454558),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold

                )
                Spacer(modifier = Modifier.height(50.dp))
            }
            Text(
                text = "Добрый вечер! Подведем итоги дня:",
                color = Color(0xFF454558),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Column {
                Row( modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top, // Выравнивание по верху
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(modifier = Modifier.weight(1f)) {
                        miniCard("Общая выручка", resultDay.total)
                        miniCard("Допы", resultDay.additional)
                        miniCard("Количество клиентов", resultDay.clietns)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        miniCard("Услуги", resultDay.service)
                        miniCard("Косметика", resultDay.cosmetic)
                        miniCard("Средний чек", resultDay.avg)

                    }
                }
                miniCard("Доход за сегодня", resultDay.today)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Сегодня 20:00",
                    color = Color(0xFF8097B1),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium

                )

            }
            Spacer(modifier = Modifier.height(8.dp))

           // }else if (!record.isAccepted) {
                Button(
                    onClick = {  },
                    modifier = Modifier
                        .height(48.dp)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(Color(0xFF666FE8)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Принять")
                }
           // }
        }
    }
}

@Composable
fun miniCard(title: String, content: String) {
    Card(
        modifier = Modifier
            .padding(3.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = "${title}",
                color = Color(0xFF8097B1),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${content}",
                color = Color(0xFF454558),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}


@Composable
fun RecordCard(record: Record, viewModel: MainViewModel) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if(!record.isAccepted) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color(0xFFA672F1), shape = CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Text(
                    text = "Через 15 минут к вам придет ${record.clientName}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Услуга",
                color = Color(0xFF8097B1),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Text(text = record.service, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Комментарий к клиенту",
                color = Color(0xFF8097B1),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Text(text = record.comment, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Егор в последний раз был в этом салоне ${
                    record.lastVisit.format(
                        DateTimeFormatter.ofPattern("dd.MM.yyyy")
                    )
                }",
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Сегодня ${record.time}",
                color = Color(0xFF8097B1),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))

            if(record.currentVist.isBefore(LocalDateTime.now())) {
                Button(
                    onClick = {  },
                    modifier = Modifier
                        .height(48.dp)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(Color(0xFFEBEEF6)),
                    enabled = false,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Вы забыли принять рекомендацию")
                }
            }else if (!record.isAccepted) {
                 Button(
                     onClick = { viewModel.processIntent(RecordIntent.onAcceptClick(record.id)) },
                     modifier = Modifier
                         .height(48.dp)
                         .fillMaxWidth(),
                     colors = ButtonDefaults.buttonColors(Color(0xFF666FE8)),
                     shape = RoundedCornerShape(8.dp)
                 ) {
                     Text(text = "Принять")
                 }
             }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Подробнее о клиенте",
                    color = Color(0xFF666FE8),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .clickable { viewModel.processIntent(RecordIntent.ViewClientInfo(record)) }
                        .padding(vertical = 8.dp)
                )
            }
        }
    }
}









