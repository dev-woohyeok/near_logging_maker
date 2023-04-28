import java.io.*;
import java.nio.file.*;
import java.util.*;

// RUSTFLAGS="-C target-cpu=apple-m1" cg build --profile dev
// RUST_LOG=debug,actix_web=debug /Users/hawaii/Desktop/nearcore/target/debug/neard --home ~/.near run
public class FileModifier {
    //                "/Users/hawaii/Desktop/nearcore/target",
//            "/Users/hawaii/Desktop/nearcore/runtime",//
//            "/Users/hawaii/Desktop/nearcore/tools",//
//            "/Users/hawaii/Desktop/nearcore/chain",//
//                    "/Users/hawaii/Desktop/nearcore/core",//
//                    "/Users/hawaii/Desktop/nearcore/test-utils",//

    static String[] s = {//
            "/Users/hawaii/Desktop/nearcore/neard",//
            "/Users/hawaii/Desktop/nearcore/debug_scripts",//
            "/Users/hawaii/Desktop/nearcore/docs",//
            "/Users/hawaii/Desktop/nearcore/genesis-tools",//
            "/Users/hawaii/Desktop/nearcore/integration-tests",//
            "/Users/hawaii/Desktop/nearcore/licenses",//
            "/Users/hawaii/Desktop/nearcore/nearcore",//
            "/Users/hawaii/Desktop/nearcore/nightly",//
            "/Users/hawaii/Desktop/nearcore/pytest",//
            "/Users/hawaii/Desktop/nearcore/scripts",//
            "/Users/hawaii/Desktop/nearcore/utils",//
    };

    public static void main(String[] args) {
        start();
//        restoreBackups(directoryPath);        // Uncomment the following line to restore the files from their backups
    }

    private static void modifyTOMLFile(File file, String targetString1, String sentenceToAdd, Path filePath) {
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            List<String> modifiedLines = new ArrayList<>();

            boolean contains_chrono = lines.stream().anyMatch(line -> line.contains("chrono"));
            boolean contains_near_stable_hasher = lines.stream().anyMatch(line -> line.contains("name = \"near-stable-hasher\""));
            System.out.println("targetString1: " + targetString1);

            // `chrono`가 없다면
            if (!contains_chrono) {
                for (String line : lines) {
                    modifiedLines.add(line);
                    if (line.contains(targetString1)) { // targetString1 = "[dependencies]"
                        System.out.println("line: " + line);
                        System.out.println("sentenceToAdd: " + sentenceToAdd);
                        modifiedLines.add(sentenceToAdd);
                    }
                }
                if (contains_near_stable_hasher) {
//                modifiedLines.addAll(lines);
//                        System.out.println("contains_near_stable_hasher: 없음!!!" + file.getName());
                    System.out.println("contains_near_stable_hasher: 없음!!!" + file.getPath());
                    modifiedLines.add(targetString1);
                    modifiedLines.add(sentenceToAdd);
//                    Files.write(file.toPath(), modifiedLines);
                }
                Files.write(file.toPath(), modifiedLines);
            }

        } catch (IOException e) {
            System.err.println("Error reading or writing file: " + file.getName() + " - " + e.getMessage());
        }
    }

    private static void modifyFile(File file, String targetString1, String sentenceToAdd) {
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            List<String> modifiedLines = new ArrayList<>();

            // 수정할 파일에 `fn`이 있다면 true
            // 없으면 false
            // 이 변수가 필요한 이유 : 종종 비어있는 파일인데 매크로가 들어갈 때가 있음.
            // 예를 들면, 파일들 중 종종 헤더만 적고 바디는 없는 경우에 해당함.
            // 아무튼 이거 안해주면 에러 나는 파일들이 한두개씩 꼭 생김
            boolean containsFn = lines.stream().anyMatch(line -> line.contains("fn"));
            boolean containsConstFn = lines.stream().anyMatch(line -> line.contains("const fn"));
            boolean contains_include_str = lines.stream().anyMatch(line -> line.contains("include_str"));
            boolean contains_special = lines.stream().anyMatch(line -> line.contains("#![no"));
            boolean contains_chrono_utc_special = lines.stream().anyMatch(line -> line.contains("use chrono::{Utc};"));
            boolean contains_chrono_utc = lines.stream().anyMatch(line -> line.contains("use chrono::Utc;"));







            /** 현재문제
             * sentenceToAdd00을 두개로 나눠야 함
             * 이유 : /Users/hawaii/Desktop/nearcore/chain/chain-primitives/src/error.rs
             * 위 파일에 chrono가 이미 있는 상태인데, 나는 조건문으로 chrono가 있다면 아예 추가 안한다고 로직을 설정함
             * 그러니까 sentenceToAdd00를 나누는걸 기존 코드에 먼저 적용하고
             * 그 후 나눈 버전을 src/error.rs 의 문제를 해결 가능하게 수정 ㄱ
             * */


            // "//!"이 이 파일 내에 있는지 없는지 찾고, 찾으면 "//!"이 마지막으로 쓰인 코드가 몇번째 줄인지 반환함
//            int lastLine_targetString1 = getLine(lines, targetString1);
//            System.out.println("lastLine_targetString1: " + lastLine_targetString1);
            int lastLine_targetString1 = -1;

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.contains(targetString1)) {
                    lastLine_targetString1 = (i + 1);
                }
            }
//            System.out.println("lastLine_targetString1: " + lastLine_targetString1);

            boolean isFind = false;

            // 파일 내부에 "fn"가 있고,
            // "const fn"가 없고,
            // "contains_include_str"가 없고,
            // "contains_special"가 없고,
            // "contains_chrono_utc_special"가 없고,
            // "contains_chrono_utc"가 없다면
            if (containsFn
                    && !containsConstFn
                    && !contains_include_str
                    && !contains_special
                    && !contains_chrono_utc_special
                    && !contains_chrono_utc
            ) {
                // 파일 내부에 "//!"가 없다면
                if (lastLine_targetString1 == -1) {
                    for (String line : lines) {
                        if (!isFind && !line.contains(targetString1)) { // targetString1 = "//!"
                            modifiedLines.add(sentenceToAdd);
                            isFind = true;
                        }
                        modifiedLines.add(line);
                    }
                    Files.write(file.toPath(), modifiedLines);
                }
                // 파일 내부에 "//!"가 있다면
                else {
                    for (int i = 0; i < lines.size(); i++) {
                        String line = lines.get(i);
                        modifiedLines.add(line);
                        if (i == lastLine_targetString1) {
                            modifiedLines.add("");
                            modifiedLines.add(sentenceToAdd);
                        }
                    }
                    Files.write(file.toPath(), modifiedLines);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading or writing file: " + file.getName() + " - " + e.getMessage());
        }
    }

    private static void modifyFile(File file, String targetString1, String targetString2, String targetString3, String targetString4, String sentenceToAdd) {
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            List<String> modifiedLines = new ArrayList<>();
            boolean containsConstFn = lines.stream().anyMatch(line -> line.contains("const fn"));
            boolean contains_include_str = lines.stream().anyMatch(line -> line.contains("include_str"));

            // 파일 내부에 "const fn"가 없다면
            if ((!containsConstFn) && (!contains_include_str)) {
                for (String line : lines) {
                    modifiedLines.add(line);
                    if (line.contains(targetString1) // fn가 있다면
                            && line.contains(targetString2)// '{' 가 있다면
                            && !line.contains(targetString3) // '}'가 없다면
                            && !line.contains(targetString4))// '///'가 없다면
                    {
                        modifiedLines.add(sentenceToAdd);
                    }
                }
            }

            Files.write(file.toPath(), modifiedLines);
        } catch (IOException e) {
            System.err.println("Error reading or writing file: " + file.getName() + " - " + e.getMessage());
        }
    }

    private static void backupFile(File file) {
        File backup = new File(file.getAbsolutePath() + ".bak");
        try {
            Files.copy(file.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error creating backup file: " + file.getName() + " - " + e.getMessage());
        }
    }

    private static void restoreBackups(String directoryPath) {
        try {
            Files.walk(Paths.get(directoryPath)).forEach(filePath -> {
                if (Files.isRegularFile(filePath) && filePath.toString().endsWith(".rs.bak")) {
                    File originalFile = new File(filePath.toString().replace(".bak", ""));
                    try {
                        Files.copy(filePath, originalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        System.err.println("Error restoring file from backup: " + filePath.getFileName() + " - " + e.getMessage());
                    }
                }
            });
        } catch (IOException e) {
            System.err.println("Error walking through the directory: " + e.getMessage());
        }
    }

    private static int getLine(List<String> lines, String targetString1) {
//        System.err.println("lines.size(): " + lines.size());
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if ((i + 1) < lines.size()) {
                String nextLine = lines.get(i + 1);
//                System.err.println("    line[" + i + "]: " + line);
//                System.err.println("nextLine[" + (i+1) + "]: " + nextLine);
                if ((line.contains(targetString1)) && (!nextLine.contains(targetString1))) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static void start() {
        for (int i = 0; i < s.length; i++) {

            try {
//            Files.walk(Paths.get(directoryPath)).forEach(filePath -> {
                Files.walk(Paths.get(s[i])).forEach(filePath -> {
                    // 특정 디렉토리 제외를 위해 필요함
                    Set<String> excludedDirectories = new HashSet<>(Arrays.asList("core",
                            "primitives-core"));
                    String parentDir = filePath.getParent().getFileName().toString();

                    if (Files.isRegularFile(filePath)) {
                        boolean exclude = false;
                        for (String dir : excludedDirectories) {
                            if (filePath.toString().contains(File.separator + dir + File.separator)) {
                                exclude = true;
                                System.out.println("parentDir : " + filePath);
                                break;
                            }
                        }


                        // // 그 디렉토리 내부에 있는 모든 파일
                        //                if (Files.isRegularFile(filePath)) {

                        // // 그 디렉토리 내부에 있는 모든 lib.rs, main.rs 파일
                        //                if (Files.isRegularFile(filePath) && (filePath.toString().equals("lib.rs") || filePath.toString().equals("main.rs"))) {

                        // // 그 디렉토리 내부에서 특정 디렉토리 제외 &&
                        // // 그 디렉토리 내부에 있는 모든 .rs 파일
//                if (!excludedDirectories.contains(parentDir) && Files.isRegularFile(filePath) && filePath.toString().endsWith(".rs")) {
                        if (!exclude && (Files.isRegularFile(filePath)//
                                && filePath.toString().endsWith(".rs")//
                                && !filePath.toString().endsWith("build.rs")//
                                && !filePath.toString().endsWith("csv_parser.rs")//
                        )) {
                            System.out.println("file : " + filePath);

                            // 백업파일 생성
                            backupFile(filePath.toFile());

                            // fn이 있는 모든 메소드에 print_~~ 삽입
                            modifyFile(filePath.toFile(), targetString10, targetString11, targetString12, targetString13, sentenceToAdd01);

                            // 파일 최상단에 macro 삽입
                            modifyFile(filePath.toFile(), targetString00, sentenceToAdd00);

                        }
                        if (!exclude && (Files.isRegularFile(filePath) //
                                && filePath.toString().endsWith(".toml")//

                        )) {
                            // 모든 .toml에 문자열 추가
                            modifyTOMLFile(filePath.toFile(), targetString20, sentenceToAdd20, filePath);
                        }
                    }
                });
            } catch (IOException e) {
                System.err.println("Error walking through the directory: " + e.getMessage());
            }
        }
    }

    static String sentenceToAdd00 = "use chrono::{Utc};\n" +
            "macro_rules! print_file_path_and_function_name {\n" +
            "    () => {\n" +
            "        {\n" +
            "            fn f() {\n" +
            "            }\n" +
            "            fn type_name_of<T>(_: T) -> &'static str {\n" +
            "                std::any::type_name::<T>()\n" +
            "            }\n" +
            "            let name = type_name_of(f);\n" +
            "            let (impl_name, function_name) = match name[..name.len() - 3].rfind(\"::\") {\n" +
            "                Some(pos) => {\n" +
            "                    let impl_end = pos;\n" +
            "                    let impl_start = match name[..impl_end].rfind(\"::\") {\n" +
            "                        Some(pos) => pos + 2,\n" +
            "                        None => 0,\n" +
            "                    };\n" +
            "                    (&name[impl_start..impl_end], &name[impl_end + 2..name.len() - 3])\n" +
            "                }\n" +
            "                None => (\"\", &name[..name.len() - 3]),\n" +
            "            };\n" +
            "            println!(\"{}, {:?}, {}, {}, impl: {}\", //\n" +
            "                Utc::now().format(\"%Y-%m-%dT%H:%M:%S%.6fZ\").to_string(),//\n" +
            "                std::thread::current().id(), //\n" +
            "                format!(\"{}/ fn : {}()\", file!(), function_name), //\n" +
            "                line!(), //\n" +
            "                impl_name//\n" +
            "            ); \n" +
            "        }\n" +
            "    };\n" +
            "}\n";


    static String targetString00 = "//! ";
    static String sentenceToAdd01 = "print_file_path_and_function_name!();\n";

    static String targetString10 = " fn ";
    static String targetString11 = "{";
    static String targetString12 = "}";
    static String targetString13 = "///";

    static String targetString20 = "[dependencies]";
    static String sentenceToAdd20 = "chrono.workspace = true";

    static String directoryPath = "/Users/hawaii/Desktop/nearcore";

//    static String[] s = {//
//            "/Users/hawaii/Desktop/nearcore/neard",//
//    };

    //        static String directoryPath = "/Users/hawaii/Desktop/edit_text_test.rs";
//static         String directoryPath = "/Users/hawaii/Desktop/edit_text_test_build.rs";
//static         String directoryPath = "/Users/hawaii/Desktop/nearcore/neard";
//static         String directoryPath = "/Users/hawaii/Desktop/nearcore/chain";
//       static  String directoryPath = "/Users/hawaii/Desktop/nearcore/chain/network/src/concurrency/scope/mod.rs";

    //      static   String sentenceToAdd00 = "macro_rules! print_file_path_and_function_name {\n" +
//                "    () => {\n" +
//                "        {\n" +
//                "            fn f() {\n" +
//                "            } \n" +
//                "            fn type_name_of<T>(_: T) -> &'static str {\n" +
//                "                std::any::type_name::<T>() \n" +
//                "            } \n" +
//                "            let name = type_name_of(f); \n" +
//                "            let function_name = match &name[..name.len() - 3].rfind(':') {\n" +
//                "                Some(pos) => &name[pos + 1..name.len() - 3], \n" +
//                "                None => &name[..name.len() - 3], \n" +
//                "            }; \n" +
//                "            println!(\"File path: {}, Function: {}, Line: {}, Column: {}\", \n" +
//                "            file!(), function_name, line!(), column!() \n" +
//                "            ); \n" +
//                "        }\n" +
//                "    }; \n" +
//                "}\n";
// static        String sentenceToAdd00="macro_rules! print_file_path_and_function_name {\n" +
//                "    () => {\n" +
//                "        {\n" +
//                "            fn f() {\n" +
//                "            } \n" +
//                "            fn type_name_of<T>(_: T) -> &'static str {\n" +
//                "                std::any::type_name::<T>() \n" +
//                "            } \n" +
//                "            let name = type_name_of(f); \n" +
//                "            let function_name = match &name[..name.len() - 3].rfind(':') {\n" +
//                "                Some(pos) => &name[pos + 1..name.len() - 3], \n" +
//                "                None => &name[..name.len() - 3], \n" +
//                "            }; \n" +
//                "            let double_line = String::from(\"========================================================================\");\n" +
//                "            println!(\"{}\", &double_line);\n" +
//                "            println!(\"  {}, Line: {}, Column: {}\", \n" +
//                "                file!()+\"/\"+function_name, line!(), column!() \n" +
//                "                ); \n" +
//                "            println!(\"{}\", &double_line);\n" +
//                "        }\n" +
//                "    }; \n" +
//                "}\n";
//
}
