package termproject.studyroom.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import termproject.studyroom.domain.LectureList;
import termproject.studyroom.domain.QuestionBoard;
import termproject.studyroom.domain.QuestionComment;
import termproject.studyroom.domain.User;
import termproject.studyroom.model.QuestionBoardDTO;
import termproject.studyroom.repos.LectureListRepository;
import termproject.studyroom.repos.QuestionBoardRepository;
import termproject.studyroom.repos.QuestionCommentRepository;
import termproject.studyroom.repos.UserRepository;
import termproject.studyroom.util.NotFoundException;
import termproject.studyroom.util.ReferencedWarning;


@Service
public class QuestionBoardService {

    private final QuestionBoardRepository questionBoardRepository;
    private final UserRepository userRepository;
    private final LectureListRepository lectureListRepository;
    private final QuestionCommentRepository questionCommentRepository;

    public QuestionBoardService(final QuestionBoardRepository questionBoardRepository,
            final UserRepository userRepository, final LectureListRepository lectureListRepository,
            final QuestionCommentRepository questionCommentRepository) {
        this.questionBoardRepository = questionBoardRepository;
        this.userRepository = userRepository;
        this.lectureListRepository = lectureListRepository;
        this.questionCommentRepository = questionCommentRepository;
    }

    public List<QuestionBoardDTO> findAll() {
        final List<QuestionBoard> questionBoards = questionBoardRepository.findAll(Sort.by("questionId"));
        return questionBoards.stream()
                .map(questionBoard -> mapToDTO(questionBoard, new QuestionBoardDTO()))
                .toList();
    }

    public QuestionBoardDTO get(final Integer questionId) {
        return questionBoardRepository.findById(questionId)
                .map(questionBoard -> mapToDTO(questionBoard, new QuestionBoardDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final QuestionBoardDTO questionBoardDTO) {
        final QuestionBoard questionBoard = new QuestionBoard();
        mapToEntity(questionBoardDTO, questionBoard);
        return questionBoardRepository.save(questionBoard).getQuestionId();
    }

    public void update(final Integer questionId, final QuestionBoardDTO questionBoardDTO) {
        final QuestionBoard questionBoard = questionBoardRepository.findById(questionId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(questionBoardDTO, questionBoard);
        questionBoardRepository.save(questionBoard);
    }

    public void delete(final Integer questionId) {
        questionBoardRepository.deleteById(questionId);
    }

    private QuestionBoardDTO mapToDTO(final QuestionBoard questionBoard,
            final QuestionBoardDTO questionBoardDTO) {
        questionBoardDTO.setQuestionId(questionBoard.getQuestionId());
        questionBoardDTO.setTitle(questionBoard.getTitle());
        questionBoardDTO.setContent(questionBoard.getContent());
        questionBoardDTO.setWarnCount(questionBoard.getWarnCount());
        questionBoardDTO.setLikeCount(questionBoard.getLikeCount());
        questionBoardDTO.setAuthor(questionBoard.getAuthor() == null ? null : questionBoard.getAuthor().getStdId());
        questionBoardDTO.setLectureId(questionBoard.getLectureId() == null ? null : questionBoard.getLectureId().getLectureId());
        return questionBoardDTO;
    }

    private QuestionBoard mapToEntity(final QuestionBoardDTO questionBoardDTO,
            final QuestionBoard questionBoard) {
        questionBoard.setTitle(questionBoardDTO.getTitle());
        questionBoard.setContent(questionBoardDTO.getContent());
        questionBoard.setWarnCount(questionBoardDTO.getWarnCount());
        questionBoard.setLikeCount(questionBoardDTO.getLikeCount());
        final User author = questionBoardDTO.getAuthor() == null ? null : userRepository.findById(questionBoardDTO.getAuthor())
                .orElseThrow(() -> new NotFoundException("author not found"));
        questionBoard.setAuthor(author);
        final LectureList lectureId = questionBoardDTO.getLectureId() == null ? null : lectureListRepository.findById(questionBoardDTO.getLectureId())
                .orElseThrow(() -> new NotFoundException("lectureId not found"));
        questionBoard.setLectureId(lectureId);
        return questionBoard;
    }

    public ReferencedWarning getReferencedWarning(final Integer questionId) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final QuestionBoard questionBoard = questionBoardRepository.findById(questionId)
                .orElseThrow(NotFoundException::new);
        final QuestionComment qIdQuestionComment = questionCommentRepository.findFirstByqId(questionBoard);
        if (qIdQuestionComment != null) {
            referencedWarning.setKey("questionBoard.questionComment.qId.referenced");
            referencedWarning.addParam(qIdQuestionComment.getQcomId());
            return referencedWarning;
        }
        return null;
    }

}
