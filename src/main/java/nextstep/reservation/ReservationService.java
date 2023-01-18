package nextstep.reservation;

import nextstep.member.Member;
import nextstep.schedule.Schedule;
import nextstep.schedule.ScheduleDao;
import nextstep.support.exception.AuthorizationExcpetion;
import nextstep.support.exception.DuplicateReservationException;
import nextstep.theme.Theme;
import nextstep.theme.ThemeDao;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationService {
    public final ReservationDao reservationDao;
    public final ThemeDao themeDao;
    public final ScheduleDao scheduleDao;

    public ReservationService(ReservationDao reservationDao, ThemeDao themeDao, ScheduleDao scheduleDao) {
        this.reservationDao = reservationDao;
        this.themeDao = themeDao;
        this.scheduleDao = scheduleDao;
    }

    public Long create(ReservationRequest reservationRequest, Member member) {
        Schedule schedule = scheduleDao.findById(reservationRequest.getScheduleId());
        if (schedule == null) {
            throw new NullPointerException();
        }

        List<Reservation> reservation = reservationDao.findByScheduleId(schedule.getId());
        if (!reservation.isEmpty()) {
            throw new DuplicateReservationException();
        }

        Reservation newReservation = new Reservation(
                schedule,
                reservationRequest.getName(),
                member.getId()
        );

        return reservationDao.save(newReservation);
    }

    public List<Reservation> findAllByThemeIdAndDate(Long themeId, String date) {
        Theme theme = themeDao.findById(themeId);
        if (theme == null) {
            throw new NullPointerException();
        }

        return reservationDao.findAllByThemeIdAndDate(themeId, date);
    }

    public void deleteById(Long id, Member member) {
        Reservation reservation = reservationDao.findById(id);
        Long memberId = reservation.getMemberId();
        if (reservation == null) {
            throw new NullPointerException();
        }
        if (member.getId() != memberId) {
            throw new AuthorizationExcpetion("본인의 예약만 삭제할 수 있습니다.");
        }
        reservationDao.deleteById(id);
    }
}
