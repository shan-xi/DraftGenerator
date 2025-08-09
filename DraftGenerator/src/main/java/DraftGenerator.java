import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

public class DraftGenerator {
    private List<String> actorNameMap;

    public DraftGenerator() {
    }

    String readFile(String path) throws Exception {
        try {
            File f = new File(path);
            StringBuffer sb = null;
            String finalStr = null;
            if (f.exists()) {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
                sb = new StringBuffer();
                String tempStr = "";
                String tempName = "";
                String currentName = "";
                this.actorNameMap = new ArrayList();
                String tempActorName = "";
                Pattern p = Pattern.compile(".*?(：|:|︰)+");
                Matcher m = null;
                int line = 1;

                do {
                    tempStr = br.readLine();
                    tempStr = tempStr != null ? tempStr.replaceAll("^　+", "") : tempStr;
                    if (tempStr != null) {
                        tempStr = tempStr.replaceAll("[0-9]+(\\.|：|:|︰)[0-9]+", "").trim();
                        m = p.matcher(tempStr);
                        if (m.find()) {
                            currentName = m.group();
                            currentName = currentName.replaceAll("(：|:|︰){1}\\s{0,}", "");
                            currentName = currentName.trim();
                            if ((!this.actorNameMap.contains(currentName) || this.actorNameMap.isEmpty()) && line != 1) {
                                this.actorNameMap.add(currentName);
                            }
                        }

                        if (line == 1) {
                            tempStr = tempStr.replaceAll("(：|:|︰){1}\\s{0,}", "\t\t");
                            sb.append("\t" + tempStr.trim());
                            tempName = currentName;
                        } else if (!tempName.equals(currentName) || tempStr.indexOf("：") <= -1 && tempStr.indexOf(":") <= -1 && tempStr.indexOf("︰") <= -1) {
                            if (tempStr.indexOf("：") <= -1 && tempStr.indexOf(":") <= -1 && tempStr.indexOf("︰") <= -1) {
                                tempStr = tempStr.trim();
                                sb.append("  " + tempStr.trim());
                            } else {
                                tempStr = tempStr.replaceAll("(：|:|︰){1}\\s{0,}", "\t\t");
                                tempName = currentName;
                                sb.append("\r\n\t" + tempStr.trim());
                            }
                        } else {
                            tempStr = tempStr.replaceAll(currentName, "");
                            tempStr = tempStr.replaceAll("(：|:|︰){1}\\s{0,}", "");
                            sb.append("  " + tempStr.trim());
                        }
                    }

                    ++line;
                } while(tempStr != null);

                br.close();
            }

            if (sb != null) {
                finalStr = sb.toString();
            }

            return finalStr;
        } catch (Exception var13) {
            StackTraceElement[] ste = var13.getStackTrace();
            throw new IOException(ste[0].toString());
        }
    }

    void combineToOneLine() {
    }

    void replaceWord() {
    }

    void saveFile(String path, String content) throws IOException {
        File f = new File(path);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"));
        bw.write(content);
        bw.close();
    }

    String getActorNameMap() {
        StringBuffer sb = new StringBuffer();
        if (this.actorNameMap != null && this.actorNameMap.size() > 0) {
            Iterator<String> iterator = this.actorNameMap.iterator();

            do {
                sb.append(((String)iterator.next()).toString() + "\r\n");
            } while(iterator.hasNext());
        }

        return sb.toString();
    }

    void proccess(Map<String, String> data) throws Exception {
        try {
            String content = this.readFile((String)data.get("openFilePath"));
            if (content != null) {
                this.saveFile((String)data.get("saveFilePath"), content);
            }

            if (this.getActorNameMap() != null) {
                this.saveFile((String)data.get("saveActorNamePath"), this.getActorNameMap());
            }

        } catch (Exception var3) {
            throw var3;
        }
    }

    public static void main(String[] args) {
        DraftGenerator df = new DraftGenerator();
        Properties p = null;

        try {
            // Get the directory where the JAR file is located
            String jarFilePath = new File(DraftGenerator.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
            File jarFile = new File(jarFilePath);

            // Get the directory of the JAR (i.e., /test/ where the JAR is located)
            String baseDir = jarFile.getParent();
            String configFilePath = (args.length > 0) ? args[0] : baseDir + File.separator + "config.properties";

            Map<String, String> data = new HashMap();
            p = new Properties();
            InputStream is = new FileInputStream(configFilePath);
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            p.load(isr);

            String openFilePath = (args.length > 0) ? args[0] : baseDir + File.separator + (String)p.get("openFilePath");
            String saveFilePath = (args.length > 0) ? args[0] : baseDir + File.separator + (String)p.get("saveFilePath");
            String saveActorNamePath = (args.length > 0) ? args[0] : baseDir + File.separator + (String)p.get("saveActorNamePath");
            String log = (args.length > 0) ? args[0] : baseDir + File.separator + (String)p.get("log");
            data.put("openFilePath", openFilePath);
            data.put("saveFilePath", saveFilePath);
            data.put("saveActorNamePath", saveActorNamePath);
            data.put("logPath", log);
            System.out.println(data);
            df.proccess(data);
            JOptionPane.showMessageDialog((Component)null, p.get("okmsg").toString(), p.get("title").toString(), 1);
        } catch (Exception var7) {
            var7.printStackTrace();
            Exception e = var7;

            try {
                String baseDir = new File(DraftGenerator.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
                File jarFile = new File(baseDir);
                String logFilePath = jarFile.getParent() + File.separator + "log.txt"; // save log.txt in the same directory as JAR

                df.saveFile(logFilePath, e.getMessage());
                JOptionPane.showMessageDialog((Component)null, "Error: " + e.getMessage(), p.get("title").toString(), 0);
            } catch (Exception var6) {
            }

            StackTraceElement[] ste = var7.getStackTrace();
            JOptionPane.showMessageDialog((Component)null, "Error: " + ste[0].toString(), p.get("title").toString(), 0);
        }

    }
}