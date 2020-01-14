package all.that.matters.services;

import all.that.matters.dto.EventDto;
import all.that.matters.dto.EventsDto;
import all.that.matters.model.Event;
import all.that.matters.model.ExceededEvent;
import all.that.matters.model.Food;
import all.that.matters.model.User;
import all.that.matters.repo.EventRepo;
import all.that.matters.utils.ContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@Transactional
public class EventService {

    private EventRepo eventRepo;
    private ExceededEventService exceededEventService;

    @Autowired
    public EventService(EventRepo eventRepo, ExceededEventService exceededEventService) {
        this.eventRepo = eventRepo;
        this.exceededEventService = exceededEventService;
    }

    public List<EventDto> findForToday() {

        List<EventDto> eventDtos = new ArrayList<>();

        eventRepo.findAllConsumedFromTodayByUserId(ContextUtils.getPrincipal().getId())
                .forEach(event -> eventDtos.add(eventToEventDto(event))
                );
        return eventDtos;
    }

    public void create(Event event) {
        eventRepo.save(event);
    }

    public BigDecimal getTotalConsumedCaloriesByUserIdAndDate(Long userId, LocalDate date) {
        return eventRepo.getTotalConsumedCaloriesByUserIdAndDate(userId, date).orElse(new BigDecimal(0.0));
    }

    public void createConsumeEvent(Food food, BigDecimal amount, User user) {
        Event event = Event.builder()
                              .user(user)
                              .food(food)
                              .amount(amount)
                              .totalCalories(food.getCalories().multiply(amount))
                              .timestamp(LocalDateTime.now())
                              .build();
        create(event);
    }

    public List<Event> findAllByUserId(Long id) {
        return eventRepo.findAllByUserId(id);
    }

    public Map<LocalDate, List<EventDto>> getDayToEventDtosMapByUserId(Long userId) {
        return mapEventDtosToDay(findAllByUserId(userId));
    }

    public Map<LocalDate, ExceededEvent> getAllExceededEventsByUser(User user) {
        Map<LocalDate, ExceededEvent> dayToExceededEvents = new TreeMap<>();
        List<ExceededEvent> exceededEvents = exceededEventService.findAllByUser(user);
        exceededEvents.forEach(exceededEvent -> dayToExceededEvents.put(exceededEvent.getDate(), exceededEvent));
        return dayToExceededEvents;
    }

    public List<EventDto> eventsToDtos(List<Event> events) {
        List<EventDto> eventDtos = new ArrayList<>();
        events.forEach(event -> eventDtos.add(eventToEventDto(event)));
        return eventDtos;
    }

    private EventDto eventToEventDto(Event event) {
        return EventDto.builder()
                        .foodName(event.getFood().getName())
                        .foodAmount(event.getAmount())
                        .totalCalories(event.getTotalCalories())
                        .timestamp(event.getTimestamp()).build();
    }

    public List<EventsDto> packEventsToEventsDtos(List<Event> events) {
        List<EventsDto> eventsDtos = new ArrayList<>();
        List<LocalDate> days = getDaysOfEvents(events);

        Map<LocalDate, ExceededEvent> dayToExceededEventMap = getAllExceededEventsByUser(ContextUtils.getPrincipal());

        days.forEach(day -> generateEventsDtos(events, eventsDtos, dayToExceededEventMap, day));

        return eventsDtos;
    }

    private List<LocalDate> getDaysOfEvents(List<Event> events) {
        return events.stream()
                                       .map(event -> event.getTimestamp().toLocalDate())
                                       .collect(Collectors.toList());
    }

    private void generateEventsDtos(List<Event> events, List<EventsDto> eventsDtos,
        Map<LocalDate, ExceededEvent> dayToExceededEventMap, LocalDate day) {

        List<Event> eventsOfTheDay = pickEventsOfTheDay(events, day);
        List<EventDto> eventsDtosOfTheDay = eventsToDtos(eventsOfTheDay);
        BigDecimal totalCalories = countTotalCalories(eventsOfTheDay);

        BigDecimal exceededCalories = getExceededCaloriesIfAny(dayToExceededEventMap, day);

        eventsDtos.add(
                EventsDto.builder()
                .eventsOfTheDay(eventsDtosOfTheDay)
                .totalCalories(totalCalories).date(day)
                .exceededCalories(exceededCalories)
                .isNormExceeded(exceededCalories.intValue() != 0).build()
        );
    }

    private BigDecimal getExceededCaloriesIfAny(Map<LocalDate, ExceededEvent> dayToExceededEventMap, LocalDate day) {
        return dayToExceededEventMap.containsKey(day)
                                              ? dayToExceededEventMap.get(day).getExcessive_calories()
                                              : new BigDecimal(0);
    }

    private BigDecimal countTotalCalories(List<Event> eventsOfTheDay) {
        BigDecimal total = new BigDecimal(0.0);

        for (Event event : eventsOfTheDay) {
            total = total.add(event.getTotalCalories());
        }
        return total;
    }

    private List<Event> pickEventsOfTheDay(List<Event> events, LocalDate day) {
        return events.stream()
                .filter(event -> isFromDay(day, event))
                .collect(Collectors.toList());
    }

    private Map<LocalDate, List<EventDto>> mapEventDtosToDay(List<Event> events) {
        List<LocalDate> days = getDaysOfEvents(events);

        Map<LocalDate, List<EventDto>> dateToEventDtos = new TreeMap<>();

        days.forEach(day -> {
            dateToEventDtos.put(day, new ArrayList<EventDto>());
            sortEventsByDay(events, dateToEventDtos, day);
        });

        return dateToEventDtos;
    }

    private void sortEventsByDay(List<Event> events, Map<LocalDate, List<EventDto>> dateToEvents, LocalDate day) {
        events.stream()
                .filter(event -> isFromDay(day, event))
                .forEach(event ->
                                 dateToEvents.get(day).add(eventToEventDto(event)));
    }

    private boolean isFromDay(LocalDate day, Event event) {
        return event.getTimestamp().toLocalDate().equals(day);
    }
}
