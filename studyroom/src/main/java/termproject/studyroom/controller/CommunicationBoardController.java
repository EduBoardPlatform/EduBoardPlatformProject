package termproject.studyroom.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import termproject.studyroom.domain.LectureList;
import termproject.studyroom.domain.User;
import termproject.studyroom.model.CommunicationBoardDTO;
import termproject.studyroom.repos.LectureListRepository;
import termproject.studyroom.repos.UserRepository;
import termproject.studyroom.service.CommunicationBoardService;
import termproject.studyroom.util.CustomCollectors;
import termproject.studyroom.util.WebUtils;


@Controller
@RequestMapping("/communicationBoards")
public class CommunicationBoardController {

    private final CommunicationBoardService communicationBoardService;
    private final UserRepository userRepository;
    private final LectureListRepository lectureListRepository;

    public CommunicationBoardController(final CommunicationBoardService communicationBoardService,
            final UserRepository userRepository,
            final LectureListRepository lectureListRepository) {
        this.communicationBoardService = communicationBoardService;
        this.userRepository = userRepository;
        this.lectureListRepository = lectureListRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("authorValues", userRepository.findAll(Sort.by("stdId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::getStdId, User::getName)));
        model.addAttribute("lectureIdValues", lectureListRepository.findAll(Sort.by("lectureId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(LectureList::getLectureId, LectureList::getName)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("communicationBoards", communicationBoardService.findAll());
        return "communicationBoard/list";
    }

    @GetMapping("/add")
    public String add(
            @ModelAttribute("communicationBoard") final CommunicationBoardDTO communicationBoardDTO) {
        return "communicationBoard/add";
    }

    @PostMapping("/add")
    public String add(
            @ModelAttribute("communicationBoard") @Valid final CommunicationBoardDTO communicationBoardDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "communicationBoard/add";
        }
        communicationBoardService.create(communicationBoardDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("communicationBoard.create.success"));
        return "redirect:/communicationBoards";
    }

    @GetMapping("/edit/{comnId}")
    public String edit(@PathVariable(name = "comnId") final Integer comnId, final Model model) {
        model.addAttribute("communicationBoard", communicationBoardService.get(comnId));
        return "communicationBoard/edit";
    }

    @PostMapping("/edit/{comnId}")
    public String edit(@PathVariable(name = "comnId") final Integer comnId,
            @ModelAttribute("communicationBoard") @Valid final CommunicationBoardDTO communicationBoardDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "communicationBoard/edit";
        }
        communicationBoardService.update(comnId, communicationBoardDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("communicationBoard.update.success"));
        return "redirect:/communicationBoards";
    }

    @PostMapping("/delete/{comnId}")
    public String delete(@PathVariable(name = "comnId") final Integer comnId,
            final RedirectAttributes redirectAttributes) {
        communicationBoardService.delete(comnId);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("communicationBoard.delete.success"));
        return "redirect:/communicationBoards";
    }

}
