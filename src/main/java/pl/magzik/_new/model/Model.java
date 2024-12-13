package pl.magzik._new.model;

public class Model {

    private final ComparerService comparerService;

    public Model() {
        comparerService = new ComparerService();
    }

    public ComparerService getComparerService() {
        return comparerService;
    }
}
