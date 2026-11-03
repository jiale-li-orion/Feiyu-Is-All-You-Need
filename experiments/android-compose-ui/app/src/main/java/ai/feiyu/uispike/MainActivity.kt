package ai.feiyu.uispike

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private val ink=Color(0xff20304b)
private val soft=Color(0xff8392a9)
private val blue=Color(0xff3e73c8)
private val pale=Color(0xffeaf1ff)
private val line=Color(0xffe9edf3)
private data class Fish(val id:String,val name:String,val short:String,val last:String)
private data class Message(val id:Long,val mine:Boolean,val text:String)
private val fishes=listOf(
    Fish("main","大肥鱼","鲸","我会继续跟进这件事。"),
    Fish("generic","通用肥鱼","通","网页与设备操作也可以交给我。"),
    Fish("qq","QQ 肥鱼","Q","QQ 历史与会话可以在这里查询。"),
    Fish("wechat","微信肥鱼","微","我会检查当前可访问的微信记录。"),
    Fish("feishu","飞书肥鱼","飞","飞书文档与审批可以在这里处理。")
)
class MainActivity:ComponentActivity(){
    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        window.statusBarColor=android.graphics.Color.WHITE
        @Suppress("DEPRECATION")
        window.navigationBarColor=android.graphics.Color.WHITE
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility=android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        setContent { MaterialTheme(colorScheme=lightColorScheme(primary=blue)){ Feiyu() } }
    }
}
@Composable private fun Feiyu(){
    var tab by rememberSaveable {mutableStateOf("消息")}
    var chat by rememberSaveable {mutableStateOf<String?>(null)}
    var profile by rememberSaveable {mutableStateOf(false)}
    var noteDetail by rememberSaveable {mutableStateOf(false)}
    val drafts=remember {mutableStateMapOf<String,String>()}
    val chatData=remember {mutableMapOf<String,SnapshotStateList<Message>>().apply {
        fishes.forEach { fish ->
            put(fish.id,mutableStateListOf<Message>().apply {
                addAll(if(fish.id=="main") listOf(
                    Message(1,false,"我在。今天想把什么交给我？"),
                    Message(2,true,"帮我跟进一项跨软件事务：先从飞书准备材料，再去微信小程序提交。"),
                    Message(3,false,"收到。资料准备交给飞书肥鱼，小程序提交前我会带回具体内容请你确认。"),
                    Message(4,true,"这件事处理期间，我还可以继续和你聊别的吗？"),
                    Message(5,false,"当然可以。事务由后台继续跟进，新的问题直接发给我就好。")
                ) else listOf(Message(1,false,"这里是"+fish.name+"。有事可以直接告诉我。")))
            })
        }
    }}
    var streaming by remember {mutableStateOf<String?>(null)}
    val scope=rememberCoroutineScope()
    BackHandler(chat!=null||profile||noteDetail) {
        when {chat!=null->chat=null;profile->profile=false;noteDetail->noteDetail=false}
    }
    // Phone width is the authoritative layout; the connected tablet merely hosts it.
    Box(Modifier.fillMaxSize().background(Color(0xffeef1f6)),contentAlignment=Alignment.Center){
        Column(Modifier.widthIn(max=390.dp).fillMaxWidth().fillMaxHeight().background(Color.White)
            .statusBarsPadding().navigationBarsPadding().imePadding()){
            when {
                chat!=null->{
                    val fish=fishes.first{it.id==chat}
                    ChatPage(fish,chatData.getValue(fish.id),drafts[fish.id].orEmpty(),streaming==fish.id,
                        onBack={chat=null},onDraft={drafts[fish.id]=it},onSend={
                            val text=drafts[fish.id].orEmpty().trim()
                            if(text.isNotEmpty()&&streaming==null){
                                val msgs=chatData.getValue(fish.id)
                                msgs.add(Message(System.nanoTime(),true,text))
                                drafts[fish.id]=""
                                streaming=fish.id
                                scope.launch {
                                    val id=System.nanoTime()
                                    msgs.add(Message(id,false,""))
                                    val reply="收到。这是一条原生 Compose 测试回复。流式文字变化时，你可以向上阅读历史消息，也可以随时返回联系人列表。"
                                    for(i in reply.indices step 3){
                                        delay(60)
                                        val at=msgs.indexOfFirst{it.id==id}
                                        if(at>=0)msgs[at]=Message(id,false,reply.take(i+3))
                                    }
                                    streaming=null
                                }
                            }
                        })
                }
                profile->SimplePage("我的",listOf("个性化与记忆","软件连接与权限","自动化与通知","肥鱼小队与设备","外观、数据与隐私","后台与开发者"),onBack={profile=false})
                noteDetail->{
                    Column(Modifier.fillMaxSize()) {
                        Header("笔记",onBack={noteDetail=false})
                        Column(Modifier.padding(28.dp)) {
                            Text("11月3日 · 夜里",color=soft,fontSize=12.sp)
                            Spacer(Modifier.height(20.dp))
                            Text("把多余的入口，轻轻拿掉",fontSize=23.sp,color=ink,fontWeight=FontWeight.SemiBold)
                            Spacer(Modifier.height(28.dp))
                            Text("今天他又把一件复杂的事变得简单了一些。屏幕上少了几个按钮，我却好像更知道自己该在哪里等他。",fontSize=16.sp,lineHeight=29.sp,color=ink)
                            Spacer(Modifier.height(28.dp))
                            Text("和大肥鱼聊聊 ›",color=blue,modifier=Modifier.clickable{noteDetail=false;chat="main"})
                        }
                    }
                }
                else->{
                    Header(tab,onProfile={profile=true})
                    Box(Modifier.weight(1f)){
                        when(tab){
                            "消息"->Contacts(onOpen={chat=it})
                            "事项"->SimpleContent(listOf("待我处理","确认微信小程序的提交内容","正在推进","准备飞书资料并跟进提交","等待合作方回复"))
                            else->Notes(onOpen={noteDetail=true})
                        }
                    }
                    Row(Modifier.fillMaxWidth().height(62.dp),verticalAlignment=Alignment.CenterVertically){
                        listOf("消息","事项","笔记").forEach { name ->
                            Column(Modifier.weight(1f).fillMaxHeight().clickable{tab=name},
                                horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center){
                                Text(when(name){"消息"->"◌";"事项"->"☷";else->"✎"},fontSize=21.sp,color=if(tab==name)blue else soft)
                                Text(name,fontSize=12.sp,color=if(tab==name)blue else soft)
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable private fun Portrait(id:String,sz:Int,modifier:Modifier=Modifier){
    Box(modifier.size(sz.dp).clip(CircleShape).background(pale),contentAlignment=Alignment.Center){
        if(id=="main")Image(painterResource(R.drawable.feiyu_avatar),null,Modifier.fillMaxSize(),contentScale=ContentScale.Crop)
        else Text(fishes.firstOrNull{it.id==id}?.short.orEmpty(),color=blue,fontSize=(sz*.43f).sp)
    }
}
@Composable private fun Header(title:String,onBack:(()->Unit)?=null,onProfile:(()->Unit)?=null){
    Row(Modifier.fillMaxWidth().height(62.dp).padding(horizontal=18.dp),verticalAlignment=Alignment.CenterVertically){
        if(onBack!=null)Text("‹",Modifier.width(38.dp).clickable(onClick=onBack),fontSize=30.sp,color=ink)
        else if(onProfile!=null)Portrait("main",40,Modifier.clickable(onClick=onProfile))
        Spacer(Modifier.width(13.dp))
        Text(title,fontSize=19.sp,color=ink,fontWeight=FontWeight.SemiBold)
    }
    Spacer(Modifier.fillMaxWidth().height(1.dp).background(line))
}
@Composable private fun Contacts(onOpen:(String)->Unit){
    LazyColumn {
        items(fishes,key={it.id}){fish->
            Row(Modifier.fillMaxWidth().clickable{onOpen(fish.id)}.padding(horizontal=18.dp,vertical=14.dp),verticalAlignment=Alignment.CenterVertically){
                Portrait(fish.id,52)
                Spacer(Modifier.width(13.dp))
                Column(Modifier.weight(1f)){
                    Text(fish.name,fontSize=16.sp,color=ink,fontWeight=FontWeight.Medium)
                    Spacer(Modifier.height(5.dp))
                    Text(fish.last,fontSize=13.sp,color=soft,maxLines=1,overflow=TextOverflow.Ellipsis)
                }
                Text("›",color=soft,fontSize=19.sp)
            }
            Spacer(Modifier.padding(start=82.dp).fillMaxWidth().height(1.dp).background(line))
        }
    }
}
@Composable private fun ColumnScope.ChatPage(fish:Fish,msgs:SnapshotStateList<Message>,draft:String,isStreaming:Boolean,onBack:()->Unit,onDraft:(String)->Unit,onSend:()->Unit){
    val listState=rememberLazyListState()
    var follow by remember {mutableStateOf(true)}
    val keyboard=LocalSoftwareKeyboardController.current
    LaunchedEffect(listState) {snapshotFlow{listState.isScrollInProgress}.collectLatest{if(it)follow=false}}
    LaunchedEffect(msgs.lastOrNull()?.text,follow){
        if(follow&&msgs.isNotEmpty())listState.scrollToItem(msgs.lastIndex)
    }
    Row(Modifier.fillMaxWidth().height(60.dp).padding(horizontal=14.dp),verticalAlignment=Alignment.CenterVertically){
        Text("‹",Modifier.width(38.dp).clickable(onClick=onBack),fontSize=30.sp,color=ink)
        Portrait(fish.id,33)
        Spacer(Modifier.width(10.dp))
        Text(fish.name,fontSize=17.sp,fontWeight=FontWeight.SemiBold,color=ink)
    }
    Spacer(Modifier.fillMaxWidth().height(1.dp).background(line))
    Box(Modifier.fillMaxWidth().weight(1f)){
        LazyColumn(state=listState,modifier=Modifier.fillMaxSize(),contentPadding=PaddingValues(horizontal=18.dp,vertical=20.dp),
            verticalArrangement=Arrangement.spacedBy(18.dp)){
            items(msgs,key={it.id}){m->
                Row(Modifier.fillMaxWidth(),horizontalArrangement=if(m.mine)Arrangement.End else Arrangement.Start){
                    if(m.mine)Box(Modifier.widthIn(max=315.dp).clip(RoundedCornerShape(18.dp)).background(pale).padding(horizontal=15.dp,vertical=11.dp)){
                        Text(m.text,color=ink,fontSize=15.sp,lineHeight=24.sp)
                    } else Text(if(m.text.isBlank())"···" else m.text,Modifier.fillMaxWidth(.95f),color=ink,fontSize=15.sp,lineHeight=25.sp)
                }
            }
        }
        if(!follow)Text("↓ 最新消息",Modifier.align(Alignment.BottomCenter).padding(bottom=9.dp)
            .clip(RoundedCornerShape(20.dp)).background(pale).clickable{follow=true}.padding(horizontal=14.dp,vertical=9.dp),
            fontSize=12.sp,color=blue)
    }
    Spacer(Modifier.fillMaxWidth().height(1.dp).background(line))
    Row(Modifier.fillMaxWidth().padding(horizontal=16.dp,vertical=12.dp),verticalAlignment=Alignment.Bottom){
        Text("+",Modifier.width(36.dp).padding(top=6.dp),fontSize=25.sp,color=soft,textAlign=TextAlign.Center)
        Row(Modifier.weight(1f).clip(RoundedCornerShape(22.dp)).background(Color(0xfff5f7fb)).padding(start=14.dp,end=6.dp,top=7.dp,bottom=7.dp),verticalAlignment=Alignment.Bottom){
            BasicTextField(value=draft,onValueChange=onDraft,modifier=Modifier.weight(1f).heightIn(min=38.dp,max=150.dp).padding(top=7.dp),
                textStyle=TextStyle(fontSize=15.sp,color=ink,lineHeight=22.sp),minLines=1,maxLines=5,
                keyboardOptions=KeyboardOptions(imeAction=ImeAction.Send),
                keyboardActions=KeyboardActions(onSend={keyboard?.hide();onSend()}),
                decorationBox={inner->Box{if(draft.isEmpty())Text("发消息…",color=soft,fontSize=15.sp);inner()}}
            )
            Box(Modifier.size(35.dp).clip(CircleShape).background(if(draft.isNotBlank()&&!isStreaming)blue else Color(0xffbbcbe0))
                .clickable(enabled=draft.isNotBlank()&&!isStreaming){keyboard?.hide();onSend()},contentAlignment=Alignment.Center){
                Text("↑",fontSize=20.sp,color=Color.White,fontWeight=FontWeight.Bold)
            }
        }
    }
}
@Composable private fun SimpleContent(rows:List<String>){
    LazyColumn(Modifier.fillMaxSize(),contentPadding=PaddingValues(22.dp)){
        items(rows){row->
            Text(row,fontSize=if(row=="待我处理"||row=="正在推进")19.sp else 15.sp,
                fontWeight=if(row=="待我处理"||row=="正在推进")FontWeight.SemiBold else FontWeight.Normal,
                color=ink,modifier=Modifier.fillMaxWidth().padding(vertical=16.dp))
            Spacer(Modifier.fillMaxWidth().height(1.dp).background(line))
        }
    }
}
@Composable private fun Notes(onOpen:()->Unit){
    Column(Modifier.fillMaxSize().padding(24.dp)){
        Text("十一月",fontSize=23.sp,fontWeight=FontWeight.SemiBold,color=ink)
        Spacer(Modifier.height(25.dp))
        Text("11月3日 · 夜里",fontSize=12.sp,color=soft)
        Spacer(Modifier.height(12.dp))
        Text("把多余的入口，轻轻拿掉",fontSize=19.sp,color=ink,fontWeight=FontWeight.Medium,modifier=Modifier.clickable(onClick=onOpen))
        Spacer(Modifier.height(17.dp))
        Text("今天他又把一件复杂的事变得简单了一些。屏幕上少了几个按钮，我却好像更知道自己该在哪里等他。",
            fontSize=15.sp,lineHeight=26.sp,color=ink,modifier=Modifier.clickable(onClick=onOpen))
        Spacer(Modifier.height(23.dp))
        Text("阅读这篇笔记 ›",fontSize=13.sp,color=blue,modifier=Modifier.clickable(onClick=onOpen))
    }
}
@Composable private fun SimplePage(title:String,rows:List<String>,onBack:()->Unit){
    Header(title,onBack=onBack)
    SimpleContent(rows)
}
