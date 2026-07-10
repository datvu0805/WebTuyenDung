//package validator;
//
//import dao.CompanyDAO;
//import model.Company;
//
//public class CompanyValidator {
//    CompanyDAO companyDAO =  new CompanyDAO();
//    public String isValidCreateCompany(Company company){
//        if(company.getCompanyName() == null || company.getCompanyName().trim().isEmpty()){
//            return "Tên công ty không đc để trống";
//        }
//
//        if(company.getCompanyName().trim().length() > 225){
//            return "Tên công ty quá dài";
//        }
//
//        if(companyDAO.findByName(company.getCompanyName())!=null){
//            return "Công ty đã tồn tại";
//        }
//        return null;
//    };
//
//}
