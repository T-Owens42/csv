package com.company;

import java.io.*;
import java.util.*;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;


import static java.lang.Integer.parseInt;

public class Main {
    private static HashMap<String, List<Enrollee>> hashMap;

    public static void main(String[] args) throws IOException {
        hashMap  = new HashMap<String,List<Enrollee>>();

        //read the csv file that was created as a test
        Reader reader = new FileReader("src/com/company/testfile.csv");
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                .withHeader("userId","firstName","lastName","version","insuranceCompany")
                .withIgnoreHeaderCase()
                .withTrim());
        //arrange the provider info and get latest version
        for (CSVRecord csvRecord : csvParser) {
            // access the data in the csv record
            boolean isNewProvider = true;
            String userId = csvRecord.get("userId");
            String firstName = csvRecord.get("firstName");
            String lastName = csvRecord.get("lastName");
            Integer version = Integer.parseInt(csvRecord.get("version"));
            String insuranceCompany = csvRecord.get("insuranceCompany");

            //Set Values into Enrollee Object
            Enrollee newlyEnrolledUser = new Enrollee();
            newlyEnrolledUser.setUserId(userId);
            newlyEnrolledUser.setFirstName(firstName);
            newlyEnrolledUser.setLastName(lastName);
            newlyEnrolledUser.setVersion(version);
            newlyEnrolledUser.setInsuranceCompany(insuranceCompany);

            //Check map if insurance company already exists in map
            if(hashMap.containsKey(insuranceCompany))
            {
                //Loop through Enrollee List to check for existing User Id
                for(int i = 0; i < hashMap.get(insuranceCompany).size(); i++)
                {
                    if(hashMap.get(insuranceCompany).get(i).getUserId().equals(newlyEnrolledUser.getUserId()))
                    {
                        //Check if new Enrollee entry has latest Version
                        if(hashMap.get(insuranceCompany).get(i).getVersion() < newlyEnrolledUser.getVersion())
                        {
                            hashMap.get(insuranceCompany).set(i, newlyEnrolledUser);
                            isNewProvider = false;
                            break;
                        }
                    }
                }
                //Add New Enrollee object
                if(isNewProvider) {
                    hashMap.get(insuranceCompany).add(newlyEnrolledUser);
                }
            }
            else
            {
                //Create new Enrollee list and map to insurance company
                List<Enrollee> newEnrolleeList = new ArrayList<Enrollee>();
                newEnrolleeList.add(newlyEnrolledUser);
                hashMap.put(insuranceCompany, newEnrolleeList);
            }
        }
    }


    //File Number for File Name
    int fileNum = 1;
    //This is supposed to iterate through a list of entries. Entry set is not working for some reason.
        for((Map.Entry<String,List<Enrollee>> entry : hashMap.entrySet()))
    {
        //Sort List by a combo of Last+first name
        Collections.sort(hashMap.get(entry.getValue().get(0).getInsuranceCompany()), compareByName);

        String fileName="src/com/company/"+fileNum+".csv";
        FileWriter fw = null;
        try {
            fw = new FileWriter(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedWriter writer = new BufferedWriter(fw);
        //write the CSV files
        CSVPrinter csvPrinter = null;
        try {
            csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                    .withHeader("userId","firstName","lastName","version","insuranceCompany"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int i = 0; i < hashMap.get(entry.getValue().get(0).getInsuranceCompany()).size(); i++)
        {
            //Create new record for csv file
            try {
                csvPrinter.printRecord(entry.getValue().get(i).getUserId(), entry.getValue().get(i).getFirstName(),entry.getValue().get(i).getLastName(),
                        entry.getValue().get(i).getVersion(), entry.getValue().get(i).getInsuranceCompany());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //file name incrmentation
        fileNum++;
        try {
            csvPrinter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, List<Enrollee>> getHashMap() {
        return hashMap;
    }

    public void setHashMap(HashMap<String, List<Enrollee>> hashMap) {
        this.hashMap = hashMap;
    }

    //Comparator to sort by last+first name
    static Comparator<Enrollee> compareByName = new Comparator<Enrollee>() {
        @Override
        public int compare(Enrollee o1, Enrollee o2) {
            String name1 = o1.getLastName() + o1.getFirstName();
            String name2 = o2.getLastName() + o2.getFirstName();
            return name1.compareTo(name2);
        }
    };

}
