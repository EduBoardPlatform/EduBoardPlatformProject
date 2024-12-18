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
import termproject.studyroom.model.LectureRequestDTO;
import termproject.studyroom.repos.LectureListRepository;
import termproject.studyroom.service.LectureRequestService;
import termproject.studyroom.util.CustomCollectors;
import termproject.studyroom.util.WebUtils;


@Controller
@RequestMapping("/lectureRequests")
public class LectureRequestController {

    private final LectureRequestService lectureRequestService;
    private final LectureListRepository lectureListRepository;

    public LectureRequestController(final LectureRequestService lectureRequestService,
            final LectureListRepository lectureListRepository) {
        this.lectureRequestService = lectureRequestService;
        this.lectureListRepository = lectureListRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("lectureIdValues", lectureListRepository.findAll(Sort.by("lectureId"))
                .stream()
                .collect(CustomCollectors.toSortedMap(LectureList::getLectureId, LectureList::getName)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("lectureRequests", lectureRequestService.findAll());
        return "lectureRequest/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("lectureRequest") final LectureRequestDTO lectureRequestDTO) {
        return "lectureRequest/add";
    }

    @PostMapping("/add")
    public String add(
            @ModelAttribute("lectureRequest") @Valid final LectureRequestDTO lectureRequestDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "lectureRequest/add";
        }
        lectureRequestService.create(lectureRequestDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("lectureRequest.create.success"));
        return "redirect:/lectureRequests";
    }

    @GetMapping("/edit/{rqId}")
    public String edit(@PathVariable(name = "rqId") final Integer rqId, final Model model) {
        model.addAttribute("lectureRequest", lectureRequestService.get(rqId));
        return "lectureRequest/edit";
    }

    @PostMapping("/edit/{rqId}")
    public String edit(@PathVariable(name = "rqId") final Integer rqId,
            @ModelAttribute("lectureRequest") @Valid final LectureRequestDTO lectureRequestDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "lectureRequest/edit";
        }
        lectureRequestService.update(rqId, lectureRequestDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("lectureRequest.update.success"));
        return "redirect:/lectureRequests";
    }

    @PostMapping("/delete/{rqId}")
    public String delete(@PathVariable(name = "rqId") final Integer rqId,
            final RedirectAttributes redirectAttributes) {
        lectureRequestService.delete(rqId);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("lectureRequest.delete.success"));
        return "redirect:/lectureRequests";
    }

}
