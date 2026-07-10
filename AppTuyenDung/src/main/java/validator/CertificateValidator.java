package validator;

import constant.ScoreType;
import dto.CertificateDTO;

public class CertificateValidator {

    public static void validate(CertificateDTO dto){

        if(dto.getCertificateName()==null
                || dto.getCertificateName().isBlank()){
            throw new IllegalArgumentException("Tên chứng chỉ không được để trống.");
        }

        try{
            ScoreType.valueOf(dto.getScoreType().toUpperCase());
        }catch (Exception e){
            throw new IllegalArgumentException("Loại điểm không hợp lệ.");
        }

    }

}
