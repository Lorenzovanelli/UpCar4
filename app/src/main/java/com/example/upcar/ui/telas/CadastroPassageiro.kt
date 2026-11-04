package com.example.upcar.ui.telas

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.upcar.ui.theme.Cores
import com.example.upcar.viewmodel.PassageiroUiState
import com.example.upcar.viewmodel.PassageiroViewModel

@Composable
fun CadastroPassageiro(
    viewModel: PassageiroViewModel,
    onVoltar: () -> Unit,
    onConcluir: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CadastroPassageiroConteudo(
        uiState = uiState,
        onNomeChange = viewModel::onNomeChange,
        onRgmChange = viewModel::onRgmChange,
        onEmailChange = viewModel::onEmailChange,
        onBairroChange = viewModel::onBairroChange,
        onFotoSelecionada = viewModel::selecionarFoto,
        onAvancar = { viewModel.avancar(onConcluir) },
        onVoltar = onVoltar
    )
}

@Composable
private fun CadastroPassageiroConteudo(
    uiState: PassageiroUiState,
    onNomeChange: (String) -> Unit,
    onRgmChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onBairroChange: (String) -> Unit,
    onFotoSelecionada: (String) -> Unit,
    onAvancar: () -> Unit,
    onVoltar: () -> Unit
) {
    val seletorFoto = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) onFotoSelecionada(uri.toString()) }

    val abrirSeletor = {
        seletorFoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cores.FundoClaro)
            .statusBarsPadding()
            .imePadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onVoltar) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Cores.AzulMarca
                )
            }
            Text(
                text = "Cadastro de Passageiro",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Cores.AzulMarca
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Cores.AzulMarca)
                    .clickable { abrirSeletor() },
                contentAlignment = Alignment.Center
            ) {
                val bitmap = uiState.foto
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = "Foto do estudante",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.WorkspacePremium,
                        contentDescription = "Adicionar foto",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Adicionar foto de estudante",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Cores.AzulAcao,
                modifier = Modifier.clickable { abrirSeletor() }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                CampoCadastro(
                    rotulo = "Nome",
                    valor = uiState.nome,
                    onChange = onNomeChange,
                    placeholder = "Ex: Felipe Silva",
                    erro = uiState.erroNome
                )
                CampoCadastro(
                    rotulo = "RGM",
                    valor = uiState.rgm,
                    onChange = onRgmChange,
                    placeholder = "Ex: 293847-5",
                    teclado = KeyboardType.Number,
                    erro = uiState.erroRgm
                )
                CampoCadastro(
                    rotulo = "E-mail",
                    valor = uiState.email,
                    onChange = onEmailChange,
                    placeholder = "Ex: felipe.silva@universidade.edu",
                    teclado = KeyboardType.Email,
                    erro = uiState.erroEmail
                )
                CampoCadastro(
                    rotulo = "Bairro de Origem",
                    valor = uiState.bairro,
                    onChange = onBairroChange,
                    placeholder = "Ex: Centro",
                    erro = uiState.erroBairro
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        Box(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(20.dp)
        ) {
            Button(
                onClick = onAvancar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Cores.AzulAcao,
                    contentColor = Color.White
                )
            ) {
                Text("Avançar", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun CampoCadastro(
    rotulo: String,
    valor: String,
    onChange: (String) -> Unit,
    placeholder: String,
    erro: String?,
    teclado: KeyboardType = KeyboardType.Text
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = rotulo,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Cores.AzulMarca
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = valor,
            onValueChange = onChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = erro != null,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = teclado),
            placeholder = { Text(placeholder, fontSize = 13.sp, color = Cores.RotuloCinza) },
            supportingText = if (erro != null) {
                { Text(erro, fontSize = 11.sp, color = Cores.SosVermelho) }
            } else null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Cores.CampoFundo,
                unfocusedContainerColor = Cores.CampoFundo,
                errorContainerColor = Cores.CampoFundo,
                focusedBorderColor = Cores.AzulAcao,
                unfocusedBorderColor = Color.Transparent,
                errorBorderColor = Cores.SosVermelho,
                focusedTextColor = Cores.AzulMarca,
                unfocusedTextColor = Cores.AzulMarca,
                errorTextColor = Cores.AzulMarca,
                cursorColor = Cores.AzulAcao,
                errorCursorColor = Cores.SosVermelho
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CadastroPassageiroPreview() {
    MaterialTheme {
        CadastroPassageiroConteudo(
            uiState = PassageiroUiState(nome = "Felipe Silva", rgm = "293847-5"),
            onNomeChange = {},
            onRgmChange = {},
            onEmailChange = {},
            onBairroChange = {},
            onFotoSelecionada = {},
            onAvancar = {},
            onVoltar = {}
        )
    }
}
