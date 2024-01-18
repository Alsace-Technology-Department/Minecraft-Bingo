# MinecraftBingo
Minecraft中的物品Bingo

![物品Bingo](https://i.imgur.com/7qXBAQK.png)

## 什么是MinecraftBingo？
MinecraftBingo是Minecraft中的一种游戏模式，团队之间争夺以收集Bingo card上的物品为目标的比赛。
首个完成一行、一列或对角线的团队获胜比赛。
通过Minecraft的原生游戏体验来简单地收集物品。

## 使用方法
在服务器上拥有OP权限的玩家可以使用所有命令。
在能够开始游戏之前，玩家需要使用/team命令创建团队。
其他设置可以在下面的命令部分中进行配置。
游戏一旦开始，团队将分散在地图上（团队内的玩家将被分组在一起）。
团队合作收集Bingo card上的物品。
如果一个团队成功完成了卡片，游戏结束。
然而，当达到时间限制时，根据每个团队收集的物品数量决定获胜者。
物品数量最多的团队获胜，或游戏以平局结束。
请注意，可以右键单击Bingo card以查看需要收集的物品。

## 安装
下载[最新版本](https://github.com/Extremelyd1/minecraft-bingo/releases/latest)或使用Gradle自行编译。
该插件需要在[Spigot](https://www.spigotmc.org/)或[Paper](https://papermc.io/)服务器上运行。
将`MinecraftBingo-[version].jar`文件放入服务器的插件目录。
将`item_data.zip`解压到`<server>/plugins/MinecraftBingo/`目录。
如果成功完成，应该会有以下两个目录：
- `<server>/plugins/MinecraftBingo/item_data/`
- `<server>/plugins/MinecraftBingo/item_data/images/`

第一次运行插件时，将在`<server>/plugins/MinecraftBingo`目录中生成一个配置文件，您可以在其中编辑一些配置设置。

## 命令
#### 团队管理命令
- `/team [random|add|remove]`
  - `/team random <number of teams> [-e] [players...]` 随机将玩家分配到一组团队。如果给定了玩家列表，它将仅为这些玩家创建团队（如果使用`-e`标志，则排除这些玩家）。
  - `/team add <player name> <team name>` 将玩家添加到指定的团队
    可用的团队名称有：Red、Blue、Green、Yellow、Pink、Aqua、Orange、Gray
  - `/team remove <player name>` 从指定的团队中移除玩家

#### 游戏开始/结束
- `/start` 开始游戏
- `/end` 结束游戏

#### 配置命令
- `/pvp` 启用/禁用PvP
- `/maintenance` 启用维护模式（这将阻止所有非OP玩家加入）
- `/wincondition <full|lines|lockout> [number]` 将获胜条件更改为完成整张卡片、完成一定数量的线（行、列或对角线）或锁定物品。
  对于'lines'或'lockout'，您可以指定需要完成的线的数量，或者在多少次收集后锁定物品。
  （别名：`/wincon`）
- `/itemdistribution <S> <A> <B> <C> <D>` 更改物品分发比例，Bingo card上出现的S、A、B、C和D级物品的数量。
  这些数字必须加起来等于25。（别名：`/itemdist`、`/distribution`、`/dist`）
- `/timer <enable|disable|length>` 启用/

禁用计时器或设置计时器的长度（长度可以用小时/分钟/秒指定，例如`/timer 10m`或`/timer 1h20m30s`）

#### 杂项命令
- `/bingo` 检查卡上的物品
- `/card` 获取新的Bingo card（如果不慎丢失）
- `/reroll` 重新抽取Bingo card上的物品
- `/coordinates [message]` 将当前坐标发送给您的团队，可选附带消息（别名：`/coord`、`/coords`）
- `/all [message]` 允许玩家与游戏中的所有玩家交谈（别名：`/a`、`/g`、`/global`）
- `/teamchat [message]` 允许玩家与他们的团队成员交谈（别名：`/tc`）
- `/channel <team|global>` 允许玩家切换默认的聊天频道，以发送聊天消息（别名`/c`）
- `/join [team name]` 允许玩家根据给定的名称加入一个团队

## 世界生成
该插件提供了预生成世界以减少游戏过程中的区块生成延迟的功能。
此功能可在[Spigot](https://www.spigotmc.org/)和[Paper](https://papermc.io/)上使用，但在Paper上速度会明显更快。
以下命令可用于管理此功能：
- `/generate <start world number> <number of worlds>` 开始预生成给定数量的世界，从给定索引开始，并以zip格式存储它们
- `/generate stop` 停止预生成世界

此命令仅在配置值`pregeneration-mode.enabled`设置为`True`时有效。
预生成还要求您为要生成的世界设置世界边界。
这些边界的大小可以在配置文件中设置：`border.overworld-size`和`border.nether-size`。
此过程将生成世界边界内的所有区块，包括世界边界外2个区块的缓冲区。
完成后，overworld和nether的目录将被压缩为`world[number].zip`并放置在`<server>/plugins/MinecrftBingo/worlds/`目录中。

## 自动开始游戏
玩家在进入游戏后，人数达到配置文件中`min-players`的值时，将开始倒计时，倒计时结束后会自动开始

游戏结束后会在20s内关闭服务器，如果想实现自动重置可以使用以下shell脚本
``` shell
#!/bin/sh
script_dir="$(cd "$(dirname "$0")" && pwd)"
while [ -f "$script_dir/isstart" ]; do # 检测游戏目录下是否存在文件“isstart”，如果存在则重启，不存在则关闭
    java -jar paper-1.20.1-196.jar nogui
    rm -rf world* # 删除所有世界
done

```