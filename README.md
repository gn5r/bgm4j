# bgm4j
BGM再生、変更のサンプルプログラム

更新履歴
----
- 2018年9月30日(日) リポジトリ新規作成、初版アップロード
- 2018年9月30日(日) SEPlayerクラスを追加。コンストラクタで音声ファイルを開く。gitignoreの修正
- 2018年10月4日(木) PCA9685.javaを作成

# Sample.java
- Threadを停止(runの処理が終了)した状態でもう一度runさせると、例外が発生
- 上記例外を防ぐため、ストップ処理をmainプログラムの方でやらなければならない
- テストケースとして0〜2のwavファイルを用意。プログラム開始時に選曲
- 以後"n"キーを押すと、次の曲を再生する
- "e"キー押下でBGMを停止させ、プログラム終了

# BGMPlayer
- コンストラクタでファイル名を指定(Threadの規約上こうせざるを得ない)
- musicPlay()でフラグをセットしrunを呼び出す
- stopBGM()でフラグを反転させ、再生用クラスオブジェクトの停止をしている

# SEPlayer
- コンストラクタでファイル名を指定
- playSE()とstopSE()はセットで呼び出すこと
