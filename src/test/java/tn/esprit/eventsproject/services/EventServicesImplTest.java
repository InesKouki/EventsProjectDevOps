package tn.esprit.eventsproject.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import tn.esprit.eventsproject.entities.Event;
import tn.esprit.eventsproject.entities.Logistics;
import tn.esprit.eventsproject.entities.Participant;
import tn.esprit.eventsproject.entities.Tache;
import tn.esprit.eventsproject.repositories.EventRepository;
import tn.esprit.eventsproject.repositories.LogisticsRepository;
import tn.esprit.eventsproject.repositories.ParticipantRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.List;


class EventServicesImplTest {

    @InjectMocks
    private EventServicesImpl eventServices;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private LogisticsRepository logisticsRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addParticipant() {
        Participant participant = new Participant();
        when(participantRepository.save(participant)).thenReturn(participant);

        Participant savedParticipant = eventServices.addParticipant(participant);

        assertNotNull(savedParticipant);
        verify(participantRepository, times(1)).save(participant);
    }

    @Test
    void addAffectEvenParticipantById() {
        Participant participant = new Participant();
        participant.setIdPart(1);
        Event event = new Event();
        when(participantRepository.findById(1)).thenReturn(Optional.of(participant));
        when(eventRepository.save(event)).thenReturn(event);

        Event savedEvent = eventServices.addAffectEvenParticipant(event, 1);

        assertNotNull(savedEvent);
        verify(participantRepository, times(1)).findById(1);
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void addAffectEvenParticipant() {
        Event event = new Event();
        Participant participant1 = new Participant();
        participant1.setIdPart(1);
        Participant participant2 = new Participant();
        participant2.setIdPart(2);
        Set<Participant> participants = new HashSet<>(Arrays.asList(participant1, participant2));
        event.setParticipants(participants);

        when(participantRepository.findById(1)).thenReturn(Optional.of(participant1));
        when(participantRepository.findById(2)).thenReturn(Optional.of(participant2));
        when(eventRepository.save(event)).thenReturn(event);

        Event savedEvent = eventServices.addAffectEvenParticipant(event);

        assertNotNull(savedEvent);
        verify(participantRepository, times(1)).findById(1);
        verify(participantRepository, times(1)).findById(2);
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void addAffectLog() {
        Event event = new Event();
        Logistics logistics = new Logistics();
        String descriptionEvent = "Test Event";
        event.setDescription(descriptionEvent);
        when(eventRepository.findByDescription(descriptionEvent)).thenReturn(event);
        when(logisticsRepository.save(logistics)).thenReturn(logistics);

        Logistics savedLogistics = eventServices.addAffectLog(logistics, descriptionEvent);

        assertNotNull(savedLogistics);
        verify(eventRepository, times(1)).findByDescription(descriptionEvent);
        verify(logisticsRepository, times(1)).save(logistics);
    }

    @Test
    void getLogisticsDates() {
        LocalDate dateDebut = LocalDate.of(2023, 1, 1);
        LocalDate dateFin = LocalDate.of(2023, 12, 31);
        Event event = new Event();
        Logistics logistics = new Logistics();
        logistics.setReserve(true);
        event.setLogistics(new HashSet<>(Collections.singletonList(logistics)));
        when(eventRepository.findByDateDebutBetween(dateDebut, dateFin)).thenReturn(Collections.singletonList(event));

        List<Logistics> logisticsList = eventServices.getLogisticsDates(dateDebut, dateFin);

        assertNotNull(logisticsList);
        assertEquals(1, logisticsList.size());
        verify(eventRepository, times(1)).findByDateDebutBetween(dateDebut, dateFin);
    }

    @Test
    void calculCout() {
        Event event = new Event();
        Logistics logistics = new Logistics();
        logistics.setReserve(true);
        logistics.setPrixUnit(10.0f);
        logistics.setQuantite(2);
        event.setLogistics(new HashSet<>(Collections.singletonList(logistics)));
        List<Event> events = Collections.singletonList(event);
        when(eventRepository.findByParticipants_NomAndParticipants_PrenomAndParticipants_Tache("Tounsi", "Ahmed", Tache.ORGANISATEUR)).thenReturn(events);

        eventServices.calculCout();

        verify(eventRepository, times(1)).findByParticipants_NomAndParticipants_PrenomAndParticipants_Tache("Tounsi", "Ahmed", Tache.ORGANISATEUR);
        verify(eventRepository, times(1)).save(event);
        assertEquals(20.0f, event.getCout());
    }
}
