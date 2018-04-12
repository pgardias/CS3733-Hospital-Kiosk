package edu.wpi.cs3733d18.teamp.Database;


import edu.wpi.cs3733d18.teamp.*;
import edu.wpi.cs3733d18.teamp.Exceptions.*;
import edu.wpi.cs3733d18.teamp.Pathfinding.Edge;
import edu.wpi.cs3733d18.teamp.Pathfinding.Node;

import java.util.ArrayList;
import java.util.HashMap;

public class DBSystem {
    private static DBSystem instance;
    private DBHandler handler;
    private MapStorage storage;
    private NodeRepo nodeRepo;
    private EdgeRepo edgeRepo;
    private RequestRepo requestRepo;
    private EmployeeRepo employeeRepo;
    private RecordRepo recordRepo;
    private TimeConverter timeConverter;

    /**
     * private constructor, should only be used once
     */
    private DBSystem() {
        storage = new MapStorage();
        handler = new DBHandler();
        nodeRepo = new NodeRepo();
        edgeRepo = new EdgeRepo();
        requestRepo = new RequestRepo();
        employeeRepo = new EmployeeRepo();
        recordRepo = new RecordRepo();
        timeConverter = new TimeConverter();
    }

    /**
     * getInstance returns a new instance of DBSystem if there
     * is none, or returns the instance is there is
     *
     * @return instance of DBSystem
     */
    public static DBSystem getInstance() {
        if (instance == null) {
            instance = new DBSystem();
        }
        return instance;
    }

    // DHHandler Functions

    public void init() {
        handler.init();
    }

    public void shutdown() {
        handler.shutdown();
    }

    public void getConn() { handler.getConn(); }

    // Storage Functions

    public Boolean updateStorage() throws NodeNotFoundException, EdgeNotFoundException {
        return storage.update();
    }

    public HashMap<String, Node> getAllNodes() {
        return storage.getNodes();
    }

    public HashMap<String, Node> getNodesOfType(Node.nodeType nodeType) {
        return storage.getNodesOfType(nodeType);
    }

    public Node getOneNode(String nodeID) throws NodeNotFoundException {
        return storage.getOneNode(nodeID);
    }

    public HashMap<String, Edge> getAllEdges() {
        return storage.getEdges();
    }

    public Edge getOneEdge(String edgeID) throws EdgeNotFoundException {
        return storage.getOneEdge(edgeID);
    }

    public Boolean willEdgeOrphanANode(String edgeID) throws OrphanNodeException{
        return storage.willEdgeOrphanANode(edgeID);
    }

    // Node Repository Functions

    public Boolean createNode(Node node, Node connectedNode) throws NodeNotFoundException, EdgeNotFoundException {
        Boolean success = nodeRepo.createNode(node, connectedNode);
        storage.update();
        return success;
    }

    public Boolean modifyNode(Node node) throws NodeNotFoundException, EdgeNotFoundException {
        Boolean success = nodeRepo.modifyNode(node);
        storage.update();
        return success;
    }

    public Boolean deleteNode(String nodeID) throws NodeNotFoundException, EdgeNotFoundException {
        Boolean success = nodeRepo.deleteNode(nodeID);
        storage.update();
        return success;
    }

    //TODO might not be used
    public Boolean doesNodeExistHere(int xcoord, int ycoord) {
        return nodeRepo.doesNodeExistHere(xcoord, ycoord);
    }

    // Edge Repository Functions

    public Boolean createEdge(Edge edge) throws NodeNotFoundException, EdgeNotFoundException {
        Boolean success = edgeRepo.createEdge(edge);
        storage.update();
        return success;
    }
    public String generateID(Node node){
        return nodeRepo.generateNodeID(node, handler.getConn());
    }

    public Boolean modifyEdge(Edge edge) throws NodeNotFoundException, EdgeNotFoundException {
        Boolean success = edgeRepo.modifyEdge(edge);
        storage.update();
        return success;
    }

    public Boolean deleteEdge(String edgeID) throws NodeNotFoundException, EdgeNotFoundException {
        Boolean success = edgeRepo.deleteEdge(edgeID);
        storage.update();
        return success;
    }

    // Request Repository Functions

    public ArrayList<Request> getAllRequests() throws RequestNotFoundException {
        return requestRepo.getAllRequests();
    }

    public Request getOneRequest(int requestID) throws RequestNotFoundException {
        return requestRepo.getOneRequest(requestID);
    }

    public Boolean createRequest(Request request) {
        return requestRepo.createRequest(request);
    }

    public Boolean modifyRequest(Request request) {
        return requestRepo.modifyRequest(request);
    }

    public Boolean completeRequest(Request request) {
        return requestRepo.completeRequest(request);
    }

    public Request.requesttype StringToRequestType(String type) { return requestRepo.StringToRequestType(type); }

    public String RequestTypeToString(Request.requesttype type) { return requestRepo.RequestTypeToString(type); }

    // Employee Repository Functions

    public HashMap<String, Employee> getAllEmployees() {
        return employeeRepo.getAllEmployees();
    }

    public Employee getOneEmployee(int employeeID) throws EmployeeNotFoundException {
        return employeeRepo.getOneEmployee(employeeID);
    }

    public Boolean createEmployee(Employee employee) {
        return employeeRepo.createEmployee(employee);
    }

    public Boolean modifyEmployee(Employee employee) {
        return employeeRepo.modifyEmployee(employee);
    }

    public Boolean deleteEmployee(int employeeID) {
        return employeeRepo.deleteEmployee(employeeID);
    }

    public Employee.employeeType StringToEmployeeType(String type) { return employeeRepo.StringToEmployeeType(type); }

    public String EmployeeTypeToString(Employee.employeeType type) { return employeeRepo.EmployeeTypeToString(type); }

    public Employee checkEmployeeLogin(String username, String password) throws LoginInvalidException {
        return employeeRepo.checkEmployeeLogin(username, password);
    }

    public Employee checkAdminLogin(String username, String password) throws LoginInvalidException, AccessNotAllowedException {
        return employeeRepo.checkAdminLogin(username, password);
    }
    // Record Repository Functions

    public ArrayList<Record> getAllRecords() { return recordRepo.getAllRecords(); }

    public ArrayList<Record> getRecordType(String requestType) { return recordRepo.getRecordType(requestType); }

    public Record getSubType(String subType) { return recordRepo.getSubType(subType); }

    public Boolean handleRequest(Request request) { return recordRepo.handleRequest(request); }

    // Time Converter Functions

    public long timeDiffMinutes() { return timeConverter.timeDiffMinutes(); }

}
