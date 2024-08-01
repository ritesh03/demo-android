package com.maktoday.model;

public class ChargeDetailsResponse {

    private Data data;

    private String message;

    private String statusCode;

    public Data getData ()
    {
        return data;
    }

    public void setData (Data data)
    {
        this.data = data;
    }

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public String getStatusCode ()
    {
        return statusCode;
    }

    public void setStatusCode (String statusCode)
    {
        this.statusCode = statusCode;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [data = "+data+", message = "+message+", statusCode = "+statusCode+"]";
    }

    public class Data
    {
        private Redirect redirect;

        private String amount;

        private String product;

        private String save_card;

        private String method;

        private String threeDSecure;

        private String description;

        private String card_threeDSecure;

        private String merchant_id;

        private Source source;

        private String api_version;

        private String statement_descriptor;

        private Reference reference;

        private Post post;

        private String live_mode;

        private Response response;

        private String currency;

        private Receipt receipt;

        private String id;

        private Transaction transaction;

        private Card card;

        private String object;

        private String status;

        private Customer customer;

        public Redirect getRedirect ()
        {
            return redirect;
        }

        public void setRedirect (Redirect redirect)
        {
            this.redirect = redirect;
        }

        public String getAmount ()
        {
            return amount;
        }

        public void setAmount (String amount)
        {
            this.amount = amount;
        }

        public String getProduct ()
        {
            return product;
        }

        public void setProduct (String product)
        {
            this.product = product;
        }

        public String getSave_card ()
        {
            return save_card;
        }

        public void setSave_card (String save_card)
        {
            this.save_card = save_card;
        }

        public String getMethod ()
        {
            return method;
        }

        public void setMethod (String method)
        {
            this.method = method;
        }

        public String getThreeDSecure ()
        {
            return threeDSecure;
        }

        public void setThreeDSecure (String threeDSecure)
        {
            this.threeDSecure = threeDSecure;
        }

        public String getDescription ()
        {
            return description;
        }

        public void setDescription (String description)
        {
            this.description = description;
        }

        public String getCard_threeDSecure ()
        {
            return card_threeDSecure;
        }

        public void setCard_threeDSecure (String card_threeDSecure)
        {
            this.card_threeDSecure = card_threeDSecure;
        }

        public String getMerchant_id ()
        {
            return merchant_id;
        }

        public void setMerchant_id (String merchant_id)
        {
            this.merchant_id = merchant_id;
        }

        public Source getSource ()
        {
            return source;
        }

        public void setSource (Source source)
        {
            this.source = source;
        }

        public String getApi_version ()
        {
            return api_version;
        }

        public void setApi_version (String api_version)
        {
            this.api_version = api_version;
        }

        public String getStatement_descriptor ()
        {
            return statement_descriptor;
        }

        public void setStatement_descriptor (String statement_descriptor)
        {
            this.statement_descriptor = statement_descriptor;
        }

        public Reference getReference ()
        {
            return reference;
        }

        public void setReference (Reference reference)
        {
            this.reference = reference;
        }

        public Post getPost ()
        {
            return post;
        }

        public void setPost (Post post)
        {
            this.post = post;
        }

        public String getLive_mode ()
        {
            return live_mode;
        }

        public void setLive_mode (String live_mode)
        {
            this.live_mode = live_mode;
        }

        public Response getResponse ()
        {
            return response;
        }

        public void setResponse (Response response)
        {
            this.response = response;
        }

        public String getCurrency ()
        {
            return currency;
        }

        public void setCurrency (String currency)
        {
            this.currency = currency;
        }

        public Receipt getReceipt ()
        {
            return receipt;
        }

        public void setReceipt (Receipt receipt)
        {
            this.receipt = receipt;
        }

        public String getId ()
        {
            return id;
        }

        public void setId (String id)
        {
            this.id = id;
        }

        public Transaction getTransaction ()
        {
            return transaction;
        }

        public void setTransaction (Transaction transaction)
        {
            this.transaction = transaction;
        }

        public Card getCard ()
        {
            return card;
        }

        public void setCard (Card card)
        {
            this.card = card;
        }

        public String getObject ()
        {
            return object;
        }

        public void setObject (String object)
        {
            this.object = object;
        }

        public String getStatus ()
        {
            return status;
        }

        public void setStatus (String status)
        {
            this.status = status;
        }

        public Customer getCustomer ()
        {
            return customer;
        }

        public void setCustomer (Customer customer)
        {
            this.customer = customer;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [redirect = "+redirect+", amount = "+amount+", product = "+product+", save_card = "+save_card+", method = "+method+", threeDSecure = "+threeDSecure+", description = "+description+", card_threeDSecure = "+card_threeDSecure+", merchant_id = "+merchant_id+", source = "+source+", api_version = "+api_version+", statement_descriptor = "+statement_descriptor+", reference = "+reference+", post = "+post+", live_mode = "+live_mode+", response = "+response+", currency = "+currency+", receipt = "+receipt+", id = "+id+", transaction = "+transaction+", card = "+card+", object = "+object+", status = "+status+", customer = "+customer+"]";
        }
    }
    public class Post
    {
        private String url;

        private String status;

        public String getUrl ()
        {
            return url;
        }

        public void setUrl (String url)
        {
            this.url = url;
        }

        public String getStatus ()
        {
            return status;
        }

        public void setStatus (String status)
        {
            this.status = status;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [url = "+url+", status = "+status+"]";
        }
    }
    public class Redirect
    {
        private String url;

        private String status;

        public String getUrl ()
        {
            return url;
        }

        public void setUrl (String url)
        {
            this.url = url;
        }

        public String getStatus ()
        {
            return status;
        }

        public void setStatus (String status)
        {
            this.status = status;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [url = "+url+", status = "+status+"]";
        }
    }
    public class Source
    {
        private String payment_type;

        private String channel;

        private String id;

        private String type;

        private String payment_method;

        private String object;

        public String getPayment_type ()
        {
            return payment_type;
        }

        public void setPayment_type (String payment_type)
        {
            this.payment_type = payment_type;
        }

        public String getChannel ()
        {
            return channel;
        }

        public void setChannel (String channel)
        {
            this.channel = channel;
        }

        public String getId ()
        {
            return id;
        }

        public void setId (String id)
        {
            this.id = id;
        }

        public String getType ()
        {
            return type;
        }

        public void setType (String type)
        {
            this.type = type;
        }

        public String getPayment_method ()
        {
            return payment_method;
        }

        public void setPayment_method (String payment_method)
        {
            this.payment_method = payment_method;
        }

        public String getObject ()
        {
            return object;
        }

        public void setObject (String object)
        {
            this.object = object;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [payment_type = "+payment_type+", channel = "+channel+", id = "+id+", type = "+type+", payment_method = "+payment_method+", object = "+object+"]";
        }
    }
    public class Customer
    {
        private Phone phone;

        private String first_name;

        private String email;

        public Phone getPhone ()
        {
            return phone;
        }

        public void setPhone (Phone phone)
        {
            this.phone = phone;
        }

        public String getFirst_name ()
        {
            return first_name;
        }

        public void setFirst_name (String first_name)
        {
            this.first_name = first_name;
        }

        public String getEmail ()
        {
            return email;
        }

        public void setEmail (String email)
        {
            this.email = email;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [phone = "+phone+", first_name = "+first_name+", email = "+email+"]";
        }
    }
    public class Phone
    {
        private String country_code;

        private String number;

        public String getCountry_code ()
        {
            return country_code;
        }

        public void setCountry_code (String country_code)
        {
            this.country_code = country_code;
        }

        public String getNumber ()
        {
            return number;
        }

        public void setNumber (String number)
        {
            this.number = number;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [country_code = "+country_code+", number = "+number+"]";
        }
    }
    public class Receipt
    {
        private String sms;

        private String id;

        private String email;

        public String getSms ()
        {
            return sms;
        }

        public void setSms (String sms)
        {
            this.sms = sms;
        }

        public String getId ()
        {
            return id;
        }

        public void setId (String id)
        {
            this.id = id;
        }

        public String getEmail ()
        {
            return email;
        }

        public void setEmail (String email)
        {
            this.email = email;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [sms = "+sms+", id = "+id+", email = "+email+"]";
        }
    }
    public class Card
    {
        private String first_six;

        private String scheme;

        private String last_four;

        private String brand;

        private String object;

        public String getFirst_six ()
        {
            return first_six;
        }

        public void setFirst_six (String first_six)
        {
            this.first_six = first_six;
        }

        public String getScheme ()
        {
            return scheme;
        }

        public void setScheme (String scheme)
        {
            this.scheme = scheme;
        }

        public String getLast_four ()
        {
            return last_four;
        }

        public void setLast_four (String last_four)
        {
            this.last_four = last_four;
        }

        public String getBrand ()
        {
            return brand;
        }

        public void setBrand (String brand)
        {
            this.brand = brand;
        }

        public String getObject ()
        {
            return object;
        }

        public void setObject (String object)
        {
            this.object = object;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [first_six = "+first_six+", scheme = "+scheme+", last_four = "+last_four+", brand = "+brand+", object = "+object+"]";
        }
    }
    public class Response
    {
        private String code;

        private String message;

        public String getCode ()
        {
            return code;
        }

        public void setCode (String code)
        {
            this.code = code;
        }

        public String getMessage ()
        {
            return message;
        }

        public void setMessage (String message)
        {
            this.message = message;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [code = "+code+", message = "+message+"]";
        }
    }
    public class Reference
    {
        private String payment;

        private String track;

        private String gateway;

        private String transaction;

        private String order;

        public String getPayment ()
        {
            return payment;
        }

        public void setPayment (String payment)
        {
            this.payment = payment;
        }

        public String getTrack ()
        {
            return track;
        }

        public void setTrack (String track)
        {
            this.track = track;
        }

        public String getGateway ()
        {
            return gateway;
        }

        public void setGateway (String gateway)
        {
            this.gateway = gateway;
        }

        public String getTransaction ()
        {
            return transaction;
        }

        public void setTransaction (String transaction)
        {
            this.transaction = transaction;
        }

        public String getOrder ()
        {
            return order;
        }

        public void setOrder (String order)
        {
            this.order = order;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [payment = "+payment+", track = "+track+", gateway = "+gateway+", transaction = "+transaction+", order = "+order+"]";
        }
    }
    public class Transaction
    {
        private String amount;

        private String timezone;

        private String created;

        private String asynchronous;

        private String currency;

        private Expiry expiry;

        public String getAmount ()
        {
            return amount;
        }

        public void setAmount (String amount)
        {
            this.amount = amount;
        }

        public String getTimezone ()
        {
            return timezone;
        }

        public void setTimezone (String timezone)
        {
            this.timezone = timezone;
        }

        public String getCreated ()
        {
            return created;
        }

        public void setCreated (String created)
        {
            this.created = created;
        }

        public String getAsynchronous ()
        {
            return asynchronous;
        }

        public void setAsynchronous (String asynchronous)
        {
            this.asynchronous = asynchronous;
        }

        public String getCurrency ()
        {
            return currency;
        }

        public void setCurrency (String currency)
        {
            this.currency = currency;
        }

        public Expiry getExpiry ()
        {
            return expiry;
        }

        public void setExpiry (Expiry expiry)
        {
            this.expiry = expiry;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [amount = "+amount+", timezone = "+timezone+", created = "+created+", asynchronous = "+asynchronous+", currency = "+currency+", expiry = "+expiry+"]";
        }
    }
    public class Expiry
    {
        private String period;

        private String type;

        public String getPeriod ()
        {
            return period;
        }

        public void setPeriod (String period)
        {
            this.period = period;
        }

        public String getType ()
        {
            return type;
        }

        public void setType (String type)
        {
            this.type = type;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [period = "+period+", type = "+type+"]";
        }
    }


}
