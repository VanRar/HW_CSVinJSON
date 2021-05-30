import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {

        //первое задание
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "src/main/java/data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);

        String json = listToJson(list);
        String fileNameJSON = "src/main/java/data.json";
        writeString(json, fileNameJSON);

        //второе задание
        List<Employee> listXML = parseXML("src/main/java/data.xml");

        String jsonXML = listToJson(list);
        String fileNameJSONXML = "src/main/java/data2.json";
        writeString(jsonXML, fileNameJSONXML);

    }

    private static List<Employee> parseXML(String pathName) throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(pathName));
        Node root = doc.getDocumentElement();
        System.out.println("Корневой элемент: " + root.getNodeName());

        return read(root);
    }

    private static List<Employee> read(Node node) {
        NodeList nodeList = node.getChildNodes();
        List<Employee> employeeList = new LinkedList<>();
        int count = -1;

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node_ = nodeList.item(i);
            if (Node.ELEMENT_NODE != node_.getNodeType() || node_.getTextContent().isEmpty() || node_.getTextContent().equals("\n")) {
                continue;
            }
            if(node_.getNodeName().equals("employee")){
                employeeList.add(new Employee());
                count++;
            }
            NodeList nodeList1 = node_.getChildNodes();
            for(int j = 0; j< nodeList1.getLength(); j++){
                Node node2 = nodeList1.item(j);

                if(node2.getNodeName().equals("id")){
                   employeeList.get(count).id = Long.parseLong(node2.getTextContent());
                }
                if(node2.getNodeName().equals("firstName")){
                    employeeList.get(count).firstName = node2.getTextContent();
                }
                if(node2.getNodeName().equals("lastName")){
                    employeeList.get(count).lastName = node2.getTextContent();;
                }
                if(node2.getNodeName().equals("country")){
                    employeeList.get(count).country = node2.getTextContent();
                }
                if(node2.getNodeName().equals("age")){
                    employeeList.get(count).age = Integer.parseInt(node2.getTextContent());
                }

            }
        }
        //проверим
        System.out.println(employeeList);
        return  employeeList;
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