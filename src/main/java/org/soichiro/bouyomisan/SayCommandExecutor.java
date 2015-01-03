package org.soichiro.bouyomisan;

import org.atilika.kuromoji.Token;
import org.atilika.kuromoji.Tokenizer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * SayCommandを実行するサービス
 */
public class SayCommandExecutor {

    /**
     * コンストラクタ
     */
    public SayCommandExecutor() {
    }

    /**
     * 読み上げの実行
     *
     * @param option
     */
    synchronized public void execute(SayOption option) {
        if(option.sayText == null || option.sayText.isEmpty()) return;

        String readingText = getKanaReading(option.sayText);
        System.out.println("readingText: " + readingText);

        Config conf = Config.getSingleton();
        if(!new File(conf.sayCommand).isFile()) {
            throw new IllegalStateException(
                    String.format("読み上げコマンド %s が存在しません.", conf.sayCommand));
        }

        List<String> commandList = new ArrayList<String>();
        commandList.add(conf.sayCommand);
        commandList.add("-p");
        commandList.add(option.sayVoice == null
                ? conf.sayVoice : option.sayVoice);
        commandList.add("-s");
        commandList.add(option.saySpeed == null
                ? conf.saySpeed : option.saySpeed);
        commandList.add("-b");
        commandList.add(option.sayVolume == null
                ? conf.sayVolume : option.sayVolume);
        commandList.add(readingText);
        ProcessBuilder pb = new ProcessBuilder();
        pb.command(commandList);

        try {
            pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 漢字かな変換をしたよみを取得する
     * @param text
     * @return
     */
    private String getKanaReading(String text) {
        Tokenizer tokenizer = Tokenizer.builder().build();
        List<Token> tokens = tokenizer.tokenize(text);
        StringBuffer kanas = new StringBuffer();
        for (Token token: tokens) {
            String reading = token.getReading();
            if(reading == null) {
                kanas.append(token.getSurfaceForm());
            } else {
                kanas.append(reading);
            }
        }
        return kanas.toString();
    }
}
