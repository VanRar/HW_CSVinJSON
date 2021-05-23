import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "src/main/java/data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);

        String json = listToJson(list);
        String fileNameJSON = "src/main/java/data.json";
        writeString(json, fileNameJSON);
    }

    private static void writeString(String json, String fileNameJSON) {
        try (FileWriter file = new
                FileWriter(fileNameJSON)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String listToJson(List<Employee> list) {

        Type listType = new TypeToken<List<Employee>>() {
        }.getType();

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(list, listType);

    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {

        //создаём в трай для правильного закрытия
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            /*
            ColumnPositionMappingStrategy определяет класс, к которому будут привязывать
            данные из CSV документа, а также порядок расположения полей в этом документе
             */
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            //CsvToBean создает инструмент для взаимодействия CSV документа и выбранной ранее стратегии:
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();

            //CsvToBean позволяет распарсить CSV файл в список объектов, который далее можно использовать в своих целях:
            List<Employee> staff = csv.parse();
            return staff;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}