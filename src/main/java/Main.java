import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] employee1 = "1,John,Smith,USA,25".split(",");
        String[] employee2 = "2,Inav,Petrov,RU,23".split(",");
        List<String[]> list0 = Arrays.asList(employee1, employee2);

        try (CSVWriter writer = new CSVWriter(new FileWriter("data.csv"))) {
            writer.writeAll(list0);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";

        List<Employee> list = parseCSV(columnMapping, fileName);
        System.out.println(list);

        String json = listToJson(list);
        writeString(json);

        List<Employee> list2 = parseXML("data.xml");
        String json2 = listToJson(list2);
        writeString(json2);
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> list = null;

        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();

            list = csv.parse();
            return list;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        return gson.toJson(list, listType);
    }

    public static void writeString(String json) {
        try (FileWriter writer = new FileWriter("data.json")) {
            writer.write(json);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static List<Employee> parseXML(String fileName) {
        List<Employee> employees = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File("data.xml"));

            Node root = doc.getDocumentElement();
            readNodes(root, employees);

        } catch (ParserConfigurationException | IOException | SAXException e) {
            System.out.println(e.getMessage());
        }

        return employees;
    }

    public static void readNodes(Node root, List<Employee> employees) {
        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                Employee emp = new Employee();
                Element element = (Element) node;
                NamedNodeMap map = element.getAttributes();
                setAttributesToEmployee(map, emp);

                employees.add(emp);
                readNodes(node, employees);
            }
        }
    }

    public static void setAttributesToEmployee(NamedNodeMap map, Employee emp) {
        for (int a = 0; a < map.getLength(); a++) {
            String attrName = map.item(a).getNodeName();
            String attrValue = map.item(a).getNodeValue();
            choiceAttributesName(attrName, attrValue, emp);
        }
    }

    public static void choiceAttributesName(String attrName, String attrValue, Employee emp) {
        switch (attrName) {
            case "id":
                emp.setId(Long.parseLong(attrValue));
                break;
            case "firstName":
                emp.setFirstName(attrValue);
                break;
            case "lastName":
                emp.setLastName(attrValue);
                break;
            case "country":
                emp.setCountry(attrValue);
                break;
            default:
                emp.setAge(Integer.parseInt(attrValue));
        }
    }

}
