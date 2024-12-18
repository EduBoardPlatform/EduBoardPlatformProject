package termproject.studyroom.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import termproject.studyroom.domain.LectureList;
import termproject.studyroom.domain.LectureRequest;


public interface LectureRequestRepository extends JpaRepository<LectureRequest, Integer> {

    LectureRequest findFirstByLectureId(LectureList lectureList);

}
