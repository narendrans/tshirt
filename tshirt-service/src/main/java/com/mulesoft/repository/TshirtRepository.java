package com.mulesoft.repository;

import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.mulesoft.tshirt_service.InventoryItem;
import org.mulesoft.tshirt_service.OrderTshirt;
import org.mulesoft.tshirt_service.Size;
import org.mulesoft.tshirt_service.TrackOrder;
import org.mulesoft.tshirt_service.TrackOrderResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

@Component
public class TshirtRepository {

  private static final Logger log = LoggerFactory.getLogger(TshirtRepository.class);

  @Autowired
  JdbcTemplate jdbcTemplate;

  @PostConstruct
  public void initData() {
    // The initData will be called once when the application starts to bootstrap with sample data
    // Drop the table if it exists and create it

    log.info("Creating tables");

    jdbcTemplate.execute("DROP TABLE IF EXISTS inventory_item;");
    jdbcTemplate.execute(
        "CREATE TABLE inventory_item(productCode INTEGER,size VARCHAR(3),description VARCHAR(20),count SMALLINT, PRIMARY KEY (productCode));");

    jdbcTemplate.execute("DROP TABLE IF EXISTS orders;");
    jdbcTemplate.execute(
        "CREATE TABLE orders(orderid serial, email VARCHAR(100),country VARCHAR(50), size VARCHAR(3),stateOrProvince VARCHAR(100),postalCode VARCHAR(100),name VARCHAR(100),city VARCHAR(100),address2 VARCHAR(100), address1 VARCHAR(100))AUTO_INCREMENT=1000;");

    log.info("Inserting data into inventory_item");
    // Insert data
    jdbcTemplate.execute("INSERT INTO inventory_item VALUES(4102,'L','Preuba',2);");
    jdbcTemplate.execute("INSERT INTO inventory_item VALUES(1412,'L','Foo',9);");
    jdbcTemplate.execute("INSERT INTO inventory_item VALUES(5656,'S','Bar',2);");
    jdbcTemplate.execute("INSERT INTO inventory_item VALUES(5657,'M','Preuba2',3);");
    jdbcTemplate.execute("INSERT INTO inventory_item VALUES(1411,'M','Awesome Tshirt',5);");

    OrderTshirt orderTshirt = new OrderTshirt();
    orderTshirt.setEmail("me@narendran.info");
    orderTshirt.setAddress1("1600 Amphitheatre Parkway");
    orderTshirt.setAddress2("");
    orderTshirt.setCity("Mountain View");
    orderTshirt.setCountry("UnitedStates");
    orderTshirt.setSize(Size.M);
    orderTshirt.setPostalCode("94043");
    orderTshirt.setStateOrProvince("California");

    log.info("Inserting data into orders");
    log.info("Generated key: " + orderTshirt(orderTshirt));

    log.info("Tracking an order");

    TrackOrder trackOrder = new TrackOrder();
    trackOrder.setOrderId("1000");
    trackOrder.setEmail("me@narendran.info");
    TrackOrderResponse trackOrderResponse = trackOrder(trackOrder);
    log.info("OrderId: " + trackOrderResponse.getOrderId() +", Size: " + trackOrderResponse.getSize());

    log.info("Checking if order exists for email: me@narendran.info: " + checkIfEmailExists("me@narendran.info"));

  }


  private Size getSize(String c) {

    Size s = Size.S;

    switch (c) {
      case "L":
        s = Size.L;
        break;
      case "M":
        s = Size.M;
        break;
      case "S":
        s = Size.S;
        break;
      case "XXL":
        s = Size.XXL;
        break;
      case "XL":
        s = Size.XL;
        break;
    }
    return s;
  }

  public List<InventoryItem> findAllInventories() {

    List<InventoryItem> inventories = new ArrayList<>();

    inventories = jdbcTemplate.query(
        "SELECT productCode, size, description, count FROM inventory_item",
        (rs, rowNum) -> {
          InventoryItem i = new InventoryItem();
          i.setProductCode(String.valueOf(rs.getInt(1)));
          i.setSize(getSize(rs.getString(2)));
          i.setDescription(rs.getString(3));
          i.setCount(BigInteger.valueOf(rs.getInt(4)));
          return i;
        });

    return inventories;

  }

  public long orderTshirt(OrderTshirt orderTshirt) {

    if (checkIfEmailExists(orderTshirt.getEmail())){
      return 0;
    }
    final PreparedStatementCreator psc = connection -> {

      final PreparedStatement ps = connection.prepareStatement(
          "INSERT INTO orders (email,country,size,stateOrProvince,postalCode,name,city,address2,address1) VALUES (?,?,?,?,?,?,?,?,?)",
          Statement.RETURN_GENERATED_KEYS);
      ps.setString(1, orderTshirt.getEmail());
      ps.setString(2, orderTshirt.getCountry());
      ps.setString(3, orderTshirt.getSize().value());
      ps.setString(4, orderTshirt.getStateOrProvince());
      ps.setString(5, orderTshirt.getPostalCode());
      ps.setString(6, orderTshirt.getName());
      ps.setString(7, orderTshirt.getCity());
      ps.setString(8, orderTshirt.getAddress2());
      ps.setString(9, orderTshirt.getAddress1());
      return ps;
    };

    final KeyHolder holder = new GeneratedKeyHolder();

    jdbcTemplate.update(psc, holder);

    final long newNameId = holder.getKey().longValue();
    return newNameId;

  }

  public TrackOrderResponse trackOrder(TrackOrder trackOrder) {

    if (!checkIfEmailExists(trackOrder.getEmail())){
      return null;
    }
    return (TrackOrderResponse) jdbcTemplate.queryForObject(
        "SELECT orderid, size from orders WHERE orderid = ?", new Object[]{trackOrder.getOrderId()},
        new BeanPropertyRowMapper(TrackOrderResponse.class));

  }

  private boolean checkIfEmailExists(String email){
    Integer cnt = jdbcTemplate.queryForObject(
        "SELECT count(*) FROM orders WHERE email = ?", Integer.class, email);
    return cnt != null && cnt > 0;
  }
}
