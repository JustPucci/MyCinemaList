package strategy;

import media.Media;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class OrdinaPerTitolo implements CriterioOrdinamento {

    @Override
    public List<Media> ordina(List<Media> mediaList) {
        return mediaList.stream()
                .sorted(Comparator.comparing(Media::getTitolo, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
                //Uso Comparator per il momento (tramite getTitolo di media)
    }
}