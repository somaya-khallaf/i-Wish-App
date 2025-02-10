package sl;

import dto.ContributionDTO;
import dto.UserDTO;

import java.sql.SQLException;
import java.util.ArrayList;

public class ContributionSL {
      public int Contribute(ContributionDTO Contribution) throws SQLException {
        return ContributionDAO.Contribute(Contribution);
    }
     public ArrayList<ContributionDTO> getAllContribution(UserDTO Contribution) throws SQLException {return new ArrayList<>();}

}
