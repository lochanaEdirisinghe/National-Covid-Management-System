package com.spark.ncms.service.custom.impl;

import com.spark.ncms.entity.Hospital;
import com.spark.ncms.entity.Patient;
import com.spark.ncms.repository.RepoFactory;
import com.spark.ncms.repository.RepoType;
import com.spark.ncms.repository.custom.DoctorRepository;
import com.spark.ncms.repository.custom.HospitalBedRepository;
import com.spark.ncms.repository.custom.HospitalRepository;
import com.spark.ncms.repository.custom.PatientRepository;
import com.spark.ncms.entity.HospitalBed;
import com.spark.ncms.response.HospitaBedResponse;
import com.spark.ncms.response.HospitalBedRespDto;
import com.spark.ncms.service.custom.DoctorService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DoctorServiceImpl implements DoctorService {

    private DoctorRepository doctorRepo;
    private HospitalBedRepository hospitalBedRepo;
    private PatientRepository patientRepo;
    private HospitalRepository hospitalRepo;

    public DoctorServiceImpl(){
        doctorRepo = RepoFactory.getInstance().getRepo(RepoType.DOCTOR);
        hospitalBedRepo = RepoFactory.getInstance().getRepo(RepoType.HOSPITAL_BED);
        patientRepo = RepoFactory.getInstance().getRepo(RepoType.PATIENT);
        hospitalRepo = RepoFactory.getInstance().getRepo(RepoType.HOSPITAL);
    }

    @Override
    public HospitaBedResponse getHospitalBedList(String doctorId, Connection con) throws SQLException, ClassNotFoundException{
        List<HospitalBedRespDto> bedList = new ArrayList<>();
        String hospitalId = doctorRepo.getHospitalId(doctorId, con);
        String hospitalName = hospitalRepo.getHospital(hospitalId, con).getName();
        List<HospitalBed> hospitalBedList = hospitalBedRepo.getHospitalBedList(hospitalId, con);
        for (HospitalBed hospitalBed: hospitalBedList) {
            Patient patient = patientRepo.getPatient(hospitalBed.getPatientId(), con);
            bedList.add(new HospitalBedRespDto(hospitalBed.getBedId(), hospitalBed.getPatientId(), patient.getAdmitted_by()!=null, patient.getDischarged_by()!=null));
        }
        if(!bedList.isEmpty()) {
            return new HospitaBedResponse(hospitalId, hospitalName, bedList);
        }
        return new HospitaBedResponse(hospitalId, hospitalName, null);
    }

    @Override
    public boolean isDirector(String doctorId, Connection con) throws SQLException, ClassNotFoundException {
        boolean isdirector = doctorRepo.isDirector(doctorId, con);
        return isdirector;

    }

    @Override
    public boolean updateDoctor(String doctorId, String hospitalId, Connection con) throws SQLException, ClassNotFoundException {
        boolean isUpdated = doctorRepo.updateDoctor(doctorId, hospitalId, con);
        return isUpdated;
    }


}
